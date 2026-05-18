package com.charan.readlater.data.repository.impl

import com.charan.readlater.BookmarkQueries
import com.charan.readlater.CategoryQueries
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.mappers.toCategoryDTO
import com.charan.readlater.data.mappers.toCategoryList
import com.charan.readlater.data.mappers.toBookmarkDTO
import com.charan.readlater.data.mappers.toBookmarkList

import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.data.remote.model.BookmarkDTO
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

class SyncManagerImpl(
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val supabaseRepo: SupabaseRepo
) : SyncManager {
    override suspend fun sync() = coroutineScope{
        val unSyncedItem = bookmarkRepository.getUnSyncedBookmarks()
        val isSyncEnabled = settingsDataStoreRepo.getLoginType().first() == LoginTypeEnum.GOOGLE
        if(isSyncEnabled){
            val syncItems = unSyncedItem.toBookmarkDTO(emailId = supabaseRepo.getEmailId() ?: "")
            val
            val bookmarksJob = async {
                supabaseRepo.syncAllBookmarks(syncItems).collectLatest {
                    when (it) {
                        is ProcessState.Error -> {
                            println("Sync Error for bookmark: ${it.exception}")
                        }

                        is ProcessState.Loading -> {
                            println("Sync Loading")
                        }

                        ProcessState.NotDetermined -> {
                            println("Sync NotDetermined")
                        }

                        is ProcessState.Success<List<BookmarkDTO>> -> {
                            println("Sync Success")
                            val syncedItems = it.data
                            syncedItems.forEach { bookmarkDTO ->
                                readLaterDataSourceRepo.updateSyncStatusForBookmark(
                                    id = bookmarkDTO.id
                                )
                            }
                        }
                    }
                }
            }

            val categoriesJob = async {
                val unSyncCategories = readLaterDataSourceRepo.getUnSyncedCategories()
                supabaseRepo.syncAllCategories(unSyncCategories.toCategoryDTO(email =  supabaseRepo.getEmailId() ?: "")).collectLatest {
                    when (it) {
                        is ProcessState.Error -> {
                            println("Category Sync Error: ${it.exception}")
                        }

                        is ProcessState.Loading -> {
                            println("Category Sync Loading")
                        }

                        ProcessState.NotDetermined -> {
                            println("Category Sync NotDetermined")
                        }

                        is ProcessState.Success<List<CategoryDTO>> -> {
                            println("Category Sync Success")
                            val categoriesSynced = it.data
                            categoriesSynced.forEach { categoryDTO ->
                                readLaterDataSourceRepo.updateSyncStatusForCategory(
                                    id = categoryDTO.id
                                )

                            }
                        }
                    }
                }


            }
            bookmarksJob.await()
            categoriesJob.await()

        }
    }

    override suspend fun fetchAndUpdate(): Flow<ProcessState<Boolean>> = channelFlow {
        send(ProcessState.Loading())

        try {
            coroutineScope {
                val bookmarksDeferred = async {
                    supabaseRepo.getAllBookmarks().collectLatest {
                        when(it){
                            is ProcessState.Success<*> -> {
                                val data = (it.data as List<BookmarkDTO>).toBookmarkList()
                                readLaterDataSourceRepo.insertItems(data)
                            }
                            is ProcessState.Error -> {
                                send(ProcessState.Error(it.exception))
                            }
                            else -> Unit
                        }
                    }
                }
                val categoriesDeferred = async {
                    supabaseRepo.getAllCategories().collectLatest {
                        when(it){
                            is ProcessState.Success<*> -> {
                                val data = (it.data as List<CategoryDTO>)
                                readLaterDataSourceRepo.insertCategories(data.toCategoryList())
                            }
                            is ProcessState.Error -> {
                                send(ProcessState.Error(it.exception))
                            }
                            else -> Unit
                        }
                    }
                }

                bookmarksDeferred.await()
                 categoriesDeferred.await()

                send(ProcessState.Success(true))
            }
        } catch (e: Exception) {
            send(ProcessState.Error(e.message.toString()))
        }
    }

    override suspend fun syncListener() = coroutineScope{
        readLaterDataSourceRepo.getUnsyncedItemsCount().collectLatest { value ->
            if(value > 0){
                println("There are $value unsynced items. Starting sync...")

                    sync()

            } else {
                println("All items are synced.")
            }
        }
    }


}

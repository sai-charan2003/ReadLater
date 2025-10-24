package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.mappers.toCategoryDTO
import com.charan.readlater.data.mappers.toCategoryEntityList
import com.charan.readlater.data.mappers.toReadLaterDTO
import com.charan.readlater.data.mappers.toReadLaterEntity
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.data.remote.model.ReadLaterDTO
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SyncManagerImpl(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val supabaseRepo: SupabaseRepo
) : SyncManager {
    override suspend fun sync() = coroutineScope{
        val unSyncedItem = readLaterDataSourceRepo.getUnSyncedItems()
        val isSyncEnabled = settingsDataStoreRepo.getLoginType().first() == LoginTypeEnum.GOOGLE
        if(isSyncEnabled){
            val syncItems = unSyncedItem.toReadLaterDTO(emailId = supabaseRepo.getEmailId() ?: "")
            println(syncItems)
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

                        is ProcessState.Success<List<ReadLaterDTO>> -> {
                            println("Sync Success")
                            val syncedItems = it.data
                            syncedItems.forEach { readLaterDTO ->
                                readLaterDataSourceRepo.updateSyncStatusForBookmark(
                                    id = readLaterDTO.id,
                                    uuid = readLaterDTO.uuid

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
                                    id = categoryDTO.id,
                                    uuid = categoryDTO.uuid
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

    override suspend fun fetchAndUpdate(): Flow<ProcessState<Boolean>> = flow {
        emit(ProcessState.Loading())

        try {
            coroutineScope {
                val bookmarksDeferred = async {
                    supabaseRepo.getAllBookmarks().collectLatest {
                        when(it){
                            is ProcessState.Success<*> -> {
                                val data = (it.data as List<ReadLaterDTO>).toReadLaterEntity()
                                readLaterDataSourceRepo.insertItems(data)
                            }
                            is ProcessState.Error -> {
                                emit(ProcessState.Error(it.exception))
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
                                readLaterDataSourceRepo.insertCategories(data.toCategoryEntityList())
                            }
                            is ProcessState.Error -> {
                                emit(ProcessState.Error(it.exception))
                            }
                            else -> Unit
                        }
                    }
                }

                bookmarksDeferred.await()
                 categoriesDeferred.await()

                emit(ProcessState.Success(true))
            }
        } catch (e: Exception) {
            emit(ProcessState.Error(e.message.toString()))
        }
    }


}
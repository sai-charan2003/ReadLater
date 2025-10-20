package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.mappers.toCategoryDTO
import com.charan.readlater.data.mappers.toReadLaterDTO
import com.charan.readlater.data.mappers.toReadLaterEntity
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
            val bookmarksJob = async {
                supabaseRepo.syncAllBookmarks(syncItems).collectLatest {
                    when (it) {
                        is ProcessState.Error -> {
                            println("Sync Error: ${it.exception}")
                        }

                        is ProcessState.Loading -> {
                            println("Sync Loading")
                        }

                        ProcessState.NotDetermined -> {
                            println("Sync NotDetermined")
                        }

                        is ProcessState.Success<*> -> {
                            println("Sync Success")
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

                        is ProcessState.Success<*> -> {
                            println("Category Sync Success")
                        }
                    }
                }


            }
            categoriesJob.await()
            bookmarksJob.await()
        }
    }

    override suspend fun fetchAndUpdate(): Flow<ProcessState<Boolean>> =
        supabaseRepo.getAllBookmarks().map { state ->
            println(state)
            when (state) {
                is ProcessState.Error -> {
                    ProcessState.Error(state.exception)
                }
                is ProcessState.Loading -> {
                    ProcessState.Loading()
                }
                ProcessState.NotDetermined -> {
                    ProcessState.NotDetermined
                }
                is ProcessState.Success<*> -> {
                    val data = (state.data as List<ReadLaterDTO>).toReadLaterEntity()
                    println("Hi $data")
                    readLaterDataSourceRepo.insertItems(data)
                    ProcessState.Success(true)
                }
            }
        }

}
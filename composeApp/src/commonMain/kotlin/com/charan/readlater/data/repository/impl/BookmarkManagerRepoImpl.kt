package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.WebScrapperRepo
import com.charan.readlater.data.mappers.toReadLaterItem
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class BookmarkManagerRepoImpl(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val readLaterSupabaseRepo: SupabaseRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val syncManager: SyncManager,
    private val webScrapperRepo: WebScrapperRepo
) : BookmarkManagerRepo {
    override suspend fun addBookmark(url: String, isDue : Boolean): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading)
        try {
            val metaData = webScrapperRepo.getWebMetaData(url)
            val readLaterItem = metaData.toReadLaterItem(url,isDue)
            readLaterDataSourceRepo.insertItem(readLaterItem)
            emit(ProcessState.Success(true))
            syncManager.sync()
        } catch (e: Exception) {
            println(e)
            emit(ProcessState.Error(e.message.toString()))
        }
    }

    override suspend fun deleteBookmark(id: String): Flow<ProcessState<Boolean>> {
        TODO("Not yet implemented")
    }
}
package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.WebScrapperRepo
import com.charan.readlater.data.mappers.toReadLaterItem
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class BookmarkManagerRepoImpl(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val readLaterSupabaseRepo: SupabaseRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val webScrapperRepo: WebScrapperRepo
) : BookmarkManagerRepo {
    override suspend fun addBookmark(url: String): Flow<ProcessState<Boolean>> {
        val progressState = MutableStateFlow<ProcessState<Boolean>>(ProcessState.Loading)
        try {
            val metaData = webScrapperRepo.getWebMetaData(url)
            println(metaData)
            val readLaterItem = metaData.toReadLaterItem(url)
            println(readLaterItem)
            readLaterDataSourceRepo.insertItem(readLaterItem)
            progressState.emit(ProcessState.Success(true))



        } catch (e: Exception) {
            progressState.emit(ProcessState.Error(e.message.toString()))
        }

        return progressState


    }

    override suspend fun deleteBookmark(id: String): Flow<ProcessState<Boolean>> {
        TODO("Not yet implemented")
    }
}
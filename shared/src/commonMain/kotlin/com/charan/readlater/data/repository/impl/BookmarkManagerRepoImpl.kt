package com.charan.readlater.data.repository.impl

import com.charan.readlater.CategoryEntity
import com.charan.readlater.ReadLaterEntity
import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.data.local.model.WebMetaData
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.WebScrapperRepo
import com.charan.readlater.data.mappers.toReadLaterItem
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.presentation.add_url.BookmarkDataUIState
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

class BookmarkManagerRepoImpl(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val readLaterSupabaseRepo: SupabaseRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val syncManager: SyncManager,
    private val webScrapperRepo: WebScrapperRepo,
) : BookmarkManagerRepo {
    override suspend fun addBookmark(bookmarkData : BookmarkDataUIState): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading())
        try {
            val metaData = webScrapperRepo.getWebMetaData(bookmarkData.url)
            val readLaterItem = metaData.toReadLaterItem(bookmarkData.url,bookmarkData.isDue, categoryUUID = bookmarkData.categoryUUID)
            readLaterDataSourceRepo.insertItem(readLaterItem)
            emit(ProcessState.Success(true))
            syncManager.sync()
        } catch (e: Exception) {
            println(e)
            emit(ProcessState.Error(e.message.toString()))
        }
    }

    override suspend fun updateBookmark(bookmarkData: BookmarkDataUIState, bookmarkUUID : String) : Flow<ProcessState<Boolean>> = flow{
        emit(ProcessState.Loading())
        try {
            val existingItem = readLaterDataSourceRepo.getBookmarkByUUID(bookmarkUUID)
            val metaData = webScrapperRepo.getWebMetaData(bookmarkData.url)
            val updatedItem = ReadLaterEntity(
                id = existingItem?.id ?: 0,
                title = metaData.title,
                url = bookmarkData.url,
                description = metaData.description,
                created_at = existingItem?.created_at ?: "",
                is_due = bookmarkData.isDue,
                image_url = metaData.imageUrl,
                isSynced = false,
                uuid = existingItem?.uuid ?: "",
                isDeleted = false,
                category_uuid = bookmarkData.categoryUUID,
                host_url = metaData.hostURL
            )
            readLaterDataSourceRepo.insertItem(updatedItem)
            emit(ProcessState.Success(true))
        } catch (e: Exception){
            println(e)
            emit(ProcessState.Error(e.message.toString()))

        }

    }

    override suspend fun deleteBookmark(uuid: String): Flow<ProcessState<Boolean>> = flow {
        emit(ProcessState.Loading())
        try {
            readLaterDataSourceRepo.deleteBookmarkByUUID(uuid)
            emit(ProcessState.Success(true))
            syncManager.sync()
        } catch (e: Exception) {
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun updateDueStatus(
        uuid: String,
        isDue: Boolean
    ): Flow<ProcessState<Boolean>> = flow{
        emit(ProcessState.Loading())
        try {
            readLaterDataSourceRepo.updateDueStatusByUUID(uuid, isDue)
            emit(ProcessState.Success(true))
            syncManager.sync()
        } catch (e: Exception) {
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun addImportBookmark(importData: List<ImportData>): Flow<ProcessState<Boolean>> = channelFlow {
        send(ProcessState.Loading(progress = 0f))

        try {
            coroutineScope {
                val total = importData.size.toLong()
                var completed = 0L

                val semaphore = Semaphore(5)

                val jobs = importData.mapIndexed { index, data ->
                    async {
                        semaphore.withPermit {
                            val metaData = webScrapperRepo.getWebMetaData(data.url ?: "")
                            val item = WebMetaData(
                                title = data.title ?: metaData.title,
                                description = metaData.description,
                                imageUrl = metaData.imageUrl,
                                hostURL = metaData.hostURL
                            )
                            val readLaterItem = item.toReadLaterItem(
                                data.url ?: "",
                                false,
                                data.created ?: ""
                            )
                            readLaterDataSourceRepo.insertItem(readLaterItem)

                            completed++
                            send(
                                ProcessState.Loading(
                                    progress = completed / total.toFloat(),
                                    total = total,
                                    current = completed
                                )
                            )
                        }
                    }
                }

                jobs.awaitAll()
            }

            send(ProcessState.Success(true))
            syncManager.sync()
        } catch (e: Exception) {
            println(e)
            send(ProcessState.Error(e.message.toString()))
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    override suspend fun createCategory(name: String,uuid : String): Flow<ProcessState<Boolean>> = flow{
        emit(ProcessState.Loading())
        readLaterDataSourceRepo.createCategory(
            CategoryEntity(
                name = name,
                uuid = uuid,
                id = 0,
                isSynced = false,
                isDeleted = false,
                createdAt = Clock.System.now().toString()

            )
        )

        emit(ProcessState.Success(true))
        syncManager.sync()


    }

    override suspend fun deleteCategory(categoryUUID: String): Flow<ProcessState<Boolean>> =flow{
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(name: String): Flow<ProcessState<Boolean>> =flow{
        TODO("Not yet implemented")
    }
}
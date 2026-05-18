package com.charan.readlater.data.repository.impl

import com.charan.readlater.Category
import com.charan.readlater.Bookmark
import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.data.local.model.WebMetaData
import com.charan.readlater.data.mappers.toBookmark
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.WebScrapperRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.utils.ProcessState
import com.charan.readlater.utils.generateUuid
import com.charan.readlater.utils.getCurrentIsoDate
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
@OptIn(ExperimentalUuidApi::class)
class BookmarkManagerRepoImpl(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val readLaterSupabaseRepo: SupabaseRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val syncManager: SyncManager,
    private val webScrapperRepo: WebScrapperRepo,
) : BookmarkManagerRepo {

    override suspend fun saveBookmark(bookmarkData : Bookmark): ProcessState<Boolean>{
        return try {
            val metaData = webScrapperRepo.getWebMetaData(bookmarkData.url)
            val bookmark = bookmarkData.copy(
                title = metaData.title.ifEmpty { bookmarkData.title },
                description = metaData.description.ifEmpty { bookmarkData.description },
                imageUrl = metaData.imageUrl.ifEmpty { bookmarkData.imageUrl },
                hostURL = metaData.hostUrl.ifEmpty { bookmarkData.hostURL },
                id = bookmarkData.id.ifEmpty { generateUuid() },
                createdAt = if(bookmarkData.id.isNotEmpty()) bookmarkData.createdAt else getCurrentIsoDate(),
                isSynced = false,
            )
            readLaterDataSourceRepo.upsetBookmark(bookmark)
            syncManager.sync()
            ProcessState.Success(true)
        } catch (e: Exception) {
            println(e)
            ProcessState.Error(e.message.toString())
        }
    }
    override suspend fun deleteBookmark(uuid: String): Flow<ProcessState<Boolean>> = flow {
        emit(ProcessState.Loading())
        try {
            readLaterDataSourceRepo.deleteItem(uuid)
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
            readLaterDataSourceRepo.updateDueStatusById(uuid, isDue)
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
                                hostUrl = metaData.hostUrl
                            )
                            val readLaterItem = item.toBookmark(
                                data.url ?: "",
                                false,
                                data.created ?: ""
                            )
                            readLaterDataSourceRepo.upsetBookmark(readLaterItem)

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

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createCategory(name: String): ProcessState<Category> {
        return try {
            readLaterDataSourceRepo.createCategory(
                Category(
                    id = generateUuid(),
                    name = name,
                    isSynced = false,
                    isDeleted = false,
                    createdAt = getCurrentIsoDate()
                )
            ).let {
                    ProcessState.Success(it)
            }
        } catch (e: Exception) {
            println(e)
            ProcessState.Error(e.message.toString())
        }
    }

    override suspend fun deleteCategory(categoryUUID: String): Flow<ProcessState<Boolean>> =flow{
        TODO("Not yet implemented")
    }

    override suspend fun updateCategory(name: String): Flow<ProcessState<Boolean>> =flow{
        TODO("Not yet implemented")
    }
}

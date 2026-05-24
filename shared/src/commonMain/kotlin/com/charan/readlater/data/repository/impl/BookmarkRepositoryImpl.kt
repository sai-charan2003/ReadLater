package com.charan.readlater.data.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.charan.readlater.Bookmark
import com.charan.readlater.BookmarkQueries
import com.charan.readlater.Category
import com.charan.readlater.data.local.model.BookmarkWithCategory
import com.charan.readlater.data.mappers.toBookmark
import com.charan.readlater.data.mappers.toBookmarkList
import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.remote.model.BookmarkDTO
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.sync.SyncManager
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.flowOn
import org.koin.core.annotation.Singleton

@Singleton(binds = [BookmarkRepository::class])
class BookmarkRepositoryImpl(
    private val bookmarkQueries: BookmarkQueries,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource,
    private val syncManager: Lazy<SyncManager>
) : BookmarkRepository {

    override suspend fun fetchBookmarks(): ProcessState<Boolean> {
        return when (val result = supabaseRemoteDataSource.getAllBookmarks()) {

            is ProcessState.Error -> {
                ProcessState.Error(result.exception)
            }

            is ProcessState.Loading -> {
                ProcessState.Loading()
            }

            ProcessState.NotDetermined -> {
                ProcessState.NotDetermined
            }

            is ProcessState.Success -> {
                val bookmarks = result.data.toBookmark()

                bookmarks.forEach { bookmark ->
                    insertBookmark(bookmark, triggerSync = false)
                }

                ProcessState.Success(true)
            }
        }
    }

    override suspend fun addBookmark(bookmark: Bookmark): Bookmark {
        insertBookmark(bookmark, triggerSync = true)
        return bookmark
    }

    private suspend fun insertBookmark(bookmark: Bookmark, triggerSync: Boolean) {
        bookmarkQueries.insertBookmark(
            id = bookmark.id,
            url = bookmark.url,
            title = bookmark.title,
            description = bookmark.description,
            createdAt = bookmark.createdAt,
            imageUrl = bookmark.imageUrl,
            isDue = bookmark.isDue,
            isSynced = bookmark.isSynced,
            isDeleted = bookmark.isDeleted,
            categoryId = bookmark.categoryId,
            hostURL = bookmark.hostURL,
            isMetaDataFetched = bookmark.isMetaDataFetched
        )
        if (triggerSync) {
            syncManager.value.syncNow()
        }
    }

    override suspend fun deleteBookmark(bookmarkId: String): Boolean {
        bookmarkQueries.deleteBookmarkById(bookmarkId)
        syncManager.value.syncNow()
        return true
    }

    override suspend fun getAllActiveBookmarks(): Flow<List<Bookmark>> {
        return bookmarkQueries.getActiveBookmarkItems().asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun getBookmarkWithCategory(bookmarkId: String): BookmarkWithCategory? {
        return bookmarkQueries.getBookmarkWithCategoryById(bookmarkId,::mapBookmarkWithCategory).executeAsOne()
    }

    private fun mapBookmarkWithCategory(
        id: String,
        url: String,
        title: String?,
        description: String?,
        createdAt: String,
        imageUrl: String?,
        isDue: Boolean,
        isSynced: Boolean,
        isDeleted: Boolean,
        categoryId: String?,
        hostURL: String?,
        isMetaDataFetched : Boolean,
        categoryName : String?,
        categoryCreateAt : String?,
        categoryIsSynced : Boolean?,
        categoryIsDeleted : Boolean?
    ): BookmarkWithCategory {
        val bookmark = Bookmark(
            id = id,
            url = url,
            title = title,
            description = description,
            createdAt = createdAt,
            imageUrl = imageUrl,
            isDue = isDue,
            isSynced = isSynced,
            isDeleted = isDeleted,
            categoryId = categoryId,
            hostURL = hostURL,
            isMetaDataFetched = isMetaDataFetched

        )
        val category = Category(
            id = categoryId ?: "",
            name = categoryName ?: "",
            createdAt = categoryCreateAt ?: "",
            isSynced = categoryIsSynced ?: false,
            isDeleted = categoryIsDeleted ?: false
        )
        return BookmarkWithCategory(
            bookmark = bookmark,
             category = category
        )
    }

    override suspend fun getUnSyncedBookmarks(): List<Bookmark> {
        return bookmarkQueries.getAllPendingSyncItems().executeAsList()
    }

    override suspend fun getPendingMetaDataFetchBookmarks(): Flow<List<Bookmark>> {
        return bookmarkQueries.getAllPendingMetadataFetchItems().asFlow().mapToList(Dispatchers.IO)
    }


    override suspend fun updateBookmarkSyncStatus(
        bookmarkId: String,
        isSynced: Boolean
    ) {
        bookmarkQueries.updateSyncStatus(
            id = bookmarkId,
            isSynced = isSynced
        )
    }

    override suspend fun updateDueStatus(bookmarkId: String, isDue: Boolean) {
        bookmarkQueries.updateDueStatus(
            isDue = isDue,
            id = bookmarkId
        )
        syncManager.value.syncNow()
    }

    override suspend fun clearAllBookmarks() {
        bookmarkQueries.delteAllBookmarkItems()
    }

    override suspend fun getAllActiveBookmarksWithCategory(): Flow<List<BookmarkWithCategory>> {
        return bookmarkQueries.getAllBookmarksWithCategory(::mapBookmarkWithCategory).asFlow().mapToList(Dispatchers.IO)
    }
}

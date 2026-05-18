package com.charan.readlater.data.repository

import com.charan.readlater.Bookmark
import com.charan.readlater.data.local.model.BookmarkWithCategory
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository  {
    suspend fun addBookmark(bookmark : Bookmark) : Bookmark

    suspend fun deleteBookmark(bookmarkId : String) : Boolean

    suspend fun getAllActiveBookmarks() : Flow<List<Bookmark>>

    suspend fun getBookmarkWithCategory(bookmarkId : String) : BookmarkWithCategory?

    suspend fun getUnSyncedBookmarks() : List<Bookmark>
}
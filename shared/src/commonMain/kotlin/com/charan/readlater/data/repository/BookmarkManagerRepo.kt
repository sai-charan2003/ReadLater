package com.charan.readlater.data.repository

import com.charan.readlater.Bookmark
import com.charan.readlater.Category
import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

/**
 * Repository is for adding the bookmark and category to the local database and syncing the data.
 * Defines methods for adding, deleting, updating, and importing bookmarks and categories.
 */
interface BookmarkManagerRepo {

    suspend fun saveBookmark(bookmarkData: Bookmark) : ProcessState<Boolean>

    suspend fun deleteBookmark(uuid: String) : Flow<ProcessState<Boolean>>

    suspend fun updateDueStatus(uuid: String, isDue: Boolean) : Flow<ProcessState<Boolean>>

    suspend fun addImportBookmark(importData : List<ImportData>) : Flow<ProcessState<Boolean>>

    suspend fun createCategory(name : String) : ProcessState<Category>

    suspend fun deleteCategory(categoryUuid : String) : Flow<ProcessState<Boolean>>

    suspend fun updateCategory(name : String) : Flow<ProcessState<Boolean>>
}
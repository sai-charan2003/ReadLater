package com.charan.readlater.data.repository

import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.presentation.home.ReadLaterUiItem
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

/**
 * Repository is for adding the bookmark and category to the local database and syncing the data.
 * Defines methods for adding, deleting, updating, and importing bookmarks and categories.
 */
interface BookmarkManagerRepo {

    suspend fun addBookmark(url : String,isDue : Boolean,categoryUUID: String) : Flow<ProcessState<Boolean>>

    suspend fun deleteBookmark(id: String) : Flow<ProcessState<Boolean>>

    suspend fun updateDueStatus(id: Long, isDue: Boolean) : Flow<ProcessState<Boolean>>

    suspend fun addImportBookmark(importData : List<ImportData>) : Flow<ProcessState<Boolean>>

    suspend fun createCategory(name : String,uuid :String) : Flow<ProcessState<Boolean>>

    suspend fun deleteCategory(categoryUUID : String) : Flow<ProcessState<Boolean>>

    suspend fun updateCategory(name : String) : Flow<ProcessState<Boolean>>
}
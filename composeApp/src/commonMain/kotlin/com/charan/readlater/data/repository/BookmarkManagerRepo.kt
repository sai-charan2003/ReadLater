package com.charan.readlater.data.repository

import com.charan.readlater.data.local.model.ImportData
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface BookmarkManagerRepo {

    suspend fun addBookmark(url : String,isDue : Boolean) : Flow<ProcessState<Boolean>>

    suspend fun deleteBookmark(id: String) : Flow<ProcessState<Boolean>>

    suspend fun updateDueStatus(id: Long, isDue: Boolean) : Flow<ProcessState<Boolean>>

    suspend fun addImportBookmark(importData : List<ImportData>) : Flow<ProcessState<Boolean>>
}
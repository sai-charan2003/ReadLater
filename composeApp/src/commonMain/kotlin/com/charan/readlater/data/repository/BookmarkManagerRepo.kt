package com.charan.readlater.data.repository

import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface BookmarkManagerRepo {

    suspend fun addBookmark(url : String) : Flow<ProcessState<Boolean>>

    suspend fun deleteBookmark(id: String) : Flow<ProcessState<Boolean>>
}
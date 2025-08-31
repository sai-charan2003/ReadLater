package com.charan.readlater.data.repository

import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface SyncManager {
    suspend fun sync()

    suspend fun fetchAndUpdate() : Flow<ProcessState<Boolean>>
}
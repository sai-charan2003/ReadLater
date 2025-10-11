package com.charan.readlater.data.repository

import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface BackupRepo {
    suspend fun importFromFile(uri : String) : Flow<ProcessState<Boolean>>
}
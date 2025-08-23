package com.charan.readlater.data.repository

import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface SupabaseRepo {

    suspend fun authorizeUser(token : String) : Flow<ProcessState<Boolean>>
    suspend fun signOutUser() : Flow<ProcessState<Boolean>>
    suspend fun insertData(): Flow<ProcessState<Boolean>>
    suspend fun updateData(): Flow<ProcessState<Boolean>>
    suspend fun deleteData(): Flow<ProcessState<Boolean>>

    suspend fun authenticationStatus(): Flow<ProcessState<Boolean>>

}
package com.charan.readlater.data.repository

import com.charan.readlater.data.remote.model.ReadLaterDTO
import com.charan.readlater.data.remote.model.UserDetails
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface SupabaseRepo {

    suspend fun loadSession()

    suspend fun authorizeUser(token : String) : Flow<ProcessState<Boolean>>
    suspend fun signOutUser() : Flow<ProcessState<Boolean>>

    suspend fun authenticationStatus(): Flow<ProcessState<Boolean>>

    suspend fun getAuthorizedUserDetails() : Flow<ProcessState<UserDetails>>

    suspend fun syncData(syncItems : List<ReadLaterDTO>) : Flow<ProcessState<Boolean>>

    suspend fun getEmailId() : String?

    suspend fun getAllBookmarks() : Flow<ProcessState<List<ReadLaterDTO>>>

}
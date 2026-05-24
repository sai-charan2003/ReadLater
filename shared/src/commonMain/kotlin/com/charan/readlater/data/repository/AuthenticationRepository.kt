package com.charan.readlater.data.repository

import com.charan.readlater.data.remote.model.AccountInfo
import com.charan.readlater.utils.ProcessState

interface AuthenticationRepository {
    suspend fun loadSession()

    suspend fun authorizeUser(token : String) : ProcessState<Boolean>

    suspend fun signOutUser() : ProcessState<Boolean>

    suspend fun getUserDetails() : AccountInfo?
}
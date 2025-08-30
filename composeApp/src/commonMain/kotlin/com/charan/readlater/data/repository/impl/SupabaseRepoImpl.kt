package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.remote.model.UserDetails
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.utils.ProcessState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SupabaseRepoImpl(
    private val readLaterSupabaseClient: SupabaseClient
) : SupabaseRepo {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun authorizeUser(token : String): Flow<ProcessState<Boolean>> {
        val authenticationProcessState = MutableStateFlow<ProcessState<Boolean>>(ProcessState.Loading)
        try {
            val rowNonce = Uuid.random().toString()
            readLaterSupabaseClient.auth.signInWith(IDToken) {
                provider = Google
                idToken = token
                nonce = rowNonce
            }
            authenticationProcessState.emit(ProcessState.Success(true))
        } catch (e: Exception){
            authenticationProcessState.emit(ProcessState.Error(e.message.toString()))
        }
        return authenticationProcessState


    }

    override suspend fun signOutUser(): Flow<ProcessState<Boolean>> = flow{

    }

    override suspend fun insertData(): Flow<ProcessState<Boolean>> = flow{

    }

    override suspend fun updateData(): Flow<ProcessState<Boolean>> =flow{

    }

    override suspend fun deleteData(): Flow<ProcessState<Boolean>> =flow{

    }

    override suspend fun authenticationStatus(): Flow<ProcessState<Boolean>> {
        return readLaterSupabaseClient.auth.sessionStatus
            .map { session ->
                when (session) {
                    is SessionStatus.Authenticated -> {
                        println("User authenticated......")
                        ProcessState.Success(true)
                    }
                    SessionStatus.Initializing -> {
                        println("Initializing Authentication......")
                        ProcessState.Loading
                    }
                    is SessionStatus.NotAuthenticated -> {
                        println("User Not authenticated......")
                        ProcessState.Success(false)
                    }
                    is SessionStatus.RefreshFailure -> {
                        println("SessionStatus.RefreshFailure: ${session.cause}")
                        ProcessState.Error(session.cause.toString())
                    }
                }
            }
            .catch { e ->
                emit(ProcessState.Error(e.message.toString()))
            }

    }

    override suspend fun getAuthorizedUserDetails(): Flow<ProcessState<UserDetails>> = flow{
        emit(ProcessState.Loading)
        try {
            val currentSession = readLaterSupabaseClient.auth.currentUserOrNull()
            val userAvatar = currentSession?.identities?.get(0)?.identityData?.get("avatar_url").toString().substringAfter("\"").substringBefore("\"")
            val userName = currentSession?.identities?.get(0)?.identityData?.get("full_name").toString().substringAfter("\"").substringBefore("\"")
            val userEmail = currentSession?.email
            emit(
                ProcessState.Success(
                    UserDetails(
                        userName = userName,
                        userEmail = userEmail ?: "",
                        imageURL = userAvatar,
                    )
                )
            )
        } catch (e: Exception){
            emit(ProcessState.Error(e.message.toString()))
        }

    }
}
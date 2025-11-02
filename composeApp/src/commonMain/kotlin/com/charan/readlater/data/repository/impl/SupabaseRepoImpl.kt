package com.charan.readlater.data.repository.impl

import com.charan.readlater.CategoryEntity
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.data.remote.model.ReadLaterDTO
import com.charan.readlater.data.remote.model.UserDetails
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.utils.ProcessState
import com.charan.readlater.utils.SupabaseAppConstatnts
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SupabaseRepoImpl(
    private val readLaterSupabaseClient: SupabaseClient,
) : SupabaseRepo {

    override suspend fun loadSession() {
        readLaterSupabaseClient.auth.loadFromStorage()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun authorizeUser(token : String): Flow<ProcessState<Boolean>> {
        val authenticationProcessState = MutableStateFlow<ProcessState<Boolean>>(ProcessState.Loading())
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
        emit(ProcessState.Loading())
        try {
            readLaterSupabaseClient.auth.signOut()
            emit(ProcessState.Success(true))
        } catch (e: Exception){
            emit(ProcessState.Error(e.message.toString()))
        }

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
                        ProcessState.Loading()
                    }
                    is SessionStatus.NotAuthenticated -> {
                        println("User Not authenticated......")
                        ProcessState.Error("User Not authenticated")
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
        emit(ProcessState.Loading())
        try {
            val currentSession = readLaterSupabaseClient.auth.currentUserOrNull()
            val userAvatar = currentSession?.identities?.get(0)?.identityData?.get("avatar_url").toString().substringAfter("\"").substringBefore("\"")
            val userName = currentSession?.identities?.get(0)?.identityData?.get("full_name").toString().substringAfter("\"").substringBefore("\"")
            val userEmail = getEmailId()
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

    override suspend fun syncAllBookmarks(syncItems: List<ReadLaterDTO>): Flow<ProcessState<List<ReadLaterDTO>>> =flow{
        emit(ProcessState.Loading())
        try {
            val insertedData = readLaterSupabaseClient
                .from(SupabaseAppConstatnts.READ_LATER_TABLE_NAME)
                .upsert(values = syncItems){
                    onConflict = "uuid"
                select()
            }.decodeList<ReadLaterDTO>()
            emit(ProcessState.Success(insertedData))
        } catch (e: Exception){
            println(e)
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun getEmailId(): String? {
        return readLaterSupabaseClient.auth.currentUserOrNull()?.email
    }

    override suspend fun getAllBookmarks(): Flow<ProcessState<List<ReadLaterDTO>>> =flow{
        emit(ProcessState.Loading())
        try {
            val emailId = getEmailId()
            if(emailId != null){
                val bookmarks = readLaterSupabaseClient.from(SupabaseAppConstatnts.READ_LATER_TABLE_NAME)
                    .select().decodeList<ReadLaterDTO>()
                emit(ProcessState.Success(bookmarks.ifEmpty { emptyList() }))
            } else {
                emit(ProcessState.Error("User not logged in"))
            }
        } catch (e: Exception){
            println("e: $e")
            emit(ProcessState.Error(e.message.toString()))
        }

    }

    override suspend fun syncAllCategories(categoryList: List<CategoryDTO>): Flow<ProcessState<List<CategoryDTO>>> =flow{

            emit(ProcessState.Loading())
            try {
                val syncedCategories = readLaterSupabaseClient.from(SupabaseAppConstatnts.CATEGORY_TABLE_NAME).insert(categoryList){
                    select()
                }.decodeList<CategoryDTO>()
                emit(ProcessState.Success(syncedCategories))
            } catch (e: Exception){
                emit(ProcessState.Error(e.message.toString()))
            }



    }


    override suspend fun getAllCategories(): Flow<ProcessState<List<CategoryDTO>>> =flow{
        emit(ProcessState.Loading())
        try {
            val allCategories = readLaterSupabaseClient.from(SupabaseAppConstatnts.CATEGORY_TABLE_NAME).select()
                .decodeList<CategoryDTO>()
            emit(ProcessState.Success(allCategories))
        } catch (e: Exception){
            emit(ProcessState.Error(e.message.toString()))
        }
    }
}
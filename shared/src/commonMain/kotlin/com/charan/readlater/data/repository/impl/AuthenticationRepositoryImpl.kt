package com.charan.readlater.data.repository.impl

import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.remote.model.AccountInfo
import com.charan.readlater.data.remote.model.UserDetailsDTO
import com.charan.readlater.data.repository.AuthenticationRepository
import com.charan.readlater.utils.ProcessState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Singleton
import kotlin.uuid.Uuid

@Singleton(binds = [AuthenticationRepository::class])
class AuthenticationRepositoryImpl(
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource
) : AuthenticationRepository {

    override suspend fun loadSession() {
        supabaseRemoteDataSource.loadSession()
    }

    override suspend fun authorizeUser(token: String): ProcessState<Boolean> {
        return supabaseRemoteDataSource.authorizeUser(token)
    }

    override suspend fun signOutUser(): ProcessState<Boolean> {
        return supabaseRemoteDataSource.signOutUser()

    }

    override suspend fun getUserDetails(): AccountInfo? {
        return try {
            val user = supabaseRemoteDataSource.getUserDetails()
            user?.let {
                AccountInfo(
                    name = it.name,
                    email = it.email,
                    profilePicUrl = it.avatarUrl
                )
            }
        } catch (e: Exception){
            null
        }

    }
}

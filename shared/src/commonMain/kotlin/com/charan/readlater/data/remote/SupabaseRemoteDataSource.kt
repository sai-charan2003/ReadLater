package com.charan.readlater.data.remote

import com.charan.readlater.data.remote.model.BookmarkDTO
import com.charan.readlater.data.remote.model.CategoryDTO
import com.charan.readlater.data.remote.model.MetaDataDTO
import com.charan.readlater.data.remote.model.UserDetailsDTO
import com.charan.readlater.utils.ProcessState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SupabaseRemoteDataSource (
    private val supabaseClient: SupabaseClient
){
    companion object {
        private const val BOOKMARK_TABLE = "bookmarks"
        private const val CATEGORY_TABLE = "categories"

        private const val EDGE_FUNCTION_FETCH_METADATA = "fetch-metadata"
    }

    suspend fun loadSession() {
        supabaseClient.auth.loadFromStorage()
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun authorizeUser(token : String) : ProcessState<Boolean> {
        return try {
            val rowNonce = Uuid.random().toString()
            supabaseClient.auth.signInWith(IDToken) {
                provider = Google
                idToken = token
                nonce = rowNonce
            }
            ProcessState.Success(true)
        } catch (e: Exception){
            ProcessState.Error(e.message.toString())
        }
    }
    suspend fun signOutUser() : ProcessState<Boolean>{
        return try {
            supabaseClient.auth.signOut()
            ProcessState.Success(true)
        } catch (e: Exception){
            ProcessState.Error(e.message.toString())
        }
    }

    suspend fun insertBookmarks(syncItems : List<BookmarkDTO>) : ProcessState<List<BookmarkDTO>> {
        return try {
            val insertedData = supabaseClient
                .from(BOOKMARK_TABLE)
                .upsert(values = syncItems){
                    onConflict = "id"
                    select()
                }.decodeList<BookmarkDTO>()
            ProcessState.Success(insertedData)
        } catch (e: Exception){
            println(e)
            ProcessState.Error(e.message.toString())
        }
    }

    suspend fun getAllBookmarks() : ProcessState<List<BookmarkDTO>>{
        return try {
            val emailId = getUserDetails()?.email?.takeIf { it.isNotBlank() }
            if(emailId != null){
                val bookmarks = supabaseClient.from(BOOKMARK_TABLE)
                    .select {
                        filter {
                            eq("email", emailId)
                        }
                    }
                    .decodeList<BookmarkDTO>()
               ProcessState.Success(bookmarks.ifEmpty { emptyList() })
            } else {
                ProcessState.Error("User not logged in")
            }
        } catch (e: Exception){
            println("e: $e")
            ProcessState.Error(e.message.toString())
        }
    }

    suspend fun getUserDetails() : UserDetailsDTO? {
        try {
            val userMetaDataJson = supabaseClient.auth.currentUserOrNull()?.userMetadata?.toString() ?: "{}"
            return Json.decodeFromString<UserDetailsDTO>(userMetaDataJson)

        } catch (e: Exception){
            return null
        }
    }


    suspend fun insertCategories(categoryList: List<CategoryDTO>) : ProcessState<List<CategoryDTO>> {
        return try {
            val syncedCategories = supabaseClient.from(CATEGORY_TABLE).upsert(categoryList){
                onConflict = "id"
                select()
            }.decodeList<CategoryDTO>()
            ProcessState.Success(syncedCategories)
        } catch (e: Exception){
            ProcessState.Error(e.message.toString())
        }
    }

    suspend fun getAllCategories() : ProcessState<List<CategoryDTO>>{
        return try {
            val emailId = getUserDetails()?.email?.takeIf { it.isNotBlank() }
            if (emailId != null) {
                val allCategories = supabaseClient.from(CATEGORY_TABLE)
                    .select {
                        filter {
                            eq("email", emailId)
                        }
                    }
                    .decodeList<CategoryDTO>()
                ProcessState.Success(allCategories.ifEmpty { emptyList() })
            } else {
                ProcessState.Error("User not logged in")
            }
        } catch (e: Exception){
            ProcessState.Error(e.message.toString())
        }
    }

    suspend fun fetchMetaDataForUrl(url : String) : ProcessState<MetaDataDTO> {
        try {
            val response = supabaseClient.functions.invoke(EDGE_FUNCTION_FETCH_METADATA, mapOf("url" to url))
            if(response.status.value == 200){
                val metadata = Json.decodeFromString<MetaDataDTO>(response.body())
                return ProcessState.Success(metadata)
            } else {
                return ProcessState.Error("Failed to fetch metadata")
            }

        } catch (e: Exception){
            return ProcessState.Error(e.message.toString())
        }
    }
}

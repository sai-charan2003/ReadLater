package com.charan.readlater.data.remote

import com.charan.readlater.BuildKonfig
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class ReadLaterSupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildKonfig.SUPABASE_URL ,
        supabaseKey = BuildKonfig.SUPABASE_ANONKEY
    ) {
        install(Auth)
        install(Postgrest)
        install(ComposeAuth){
            googleNativeLogin("757664231200-54035m1vk2l54bt0vvmkiugufj2nrco4.apps.googleusercontent.com")
        }
    }
}
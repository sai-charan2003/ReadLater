package com.charan.readlater.di


import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.impl.ReadLaterDataSourceImpl
import com.charan.readlater.data.repository.impl.SettingsDataStoreRepoImpl
import com.charan.readlater.data.repository.impl.SupabaseRepoImpl
import com.charan.readlater.presentation.authentication.AuthenticationViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    single { ReadLaterDatabase(driver = get<SqlDriver>()) }

    single<ReadLaterDataSourceRepo> {
        ReadLaterDataSourceImpl(db = get())
    }
    single <SupabaseClient>{
        ReadLaterSupabaseClient().client
    }
    single <SupabaseRepo>{
        SupabaseRepoImpl(get())
    }

    single <SettingsDataStoreRepo>{ SettingsDataStoreRepoImpl(get()) }
    viewModel { AuthenticationViewModel(get(),get ()) }
}
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
}
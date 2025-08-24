package com.charan.readlater.di


import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.WebScrapperRepo
import com.charan.readlater.data.repository.impl.BookmarkManagerRepoImpl
import com.charan.readlater.data.repository.impl.ReadLaterDataSourceImpl
import com.charan.readlater.data.repository.impl.SettingsDataStoreRepoImpl
import com.charan.readlater.data.repository.impl.SupabaseRepoImpl
import com.charan.readlater.data.repository.impl.WebScrapperRepoImpl
import com.charan.readlater.presentation.home.HomeScreenViewModel
import com.charan.readlater.presentation.authentication.AuthenticationViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
val appModule = module {
    single { ReadLaterDatabase(
        driver = get<SqlDriver>(),
    ) }


    single<ReadLaterDataSourceRepo> {
        ReadLaterDataSourceImpl(db = get())
    }
    single <SupabaseClient>{
        ReadLaterSupabaseClient().client
    }
    single <SupabaseRepo>{
        SupabaseRepoImpl(get())
    }
    single <WebScrapperRepo>{ WebScrapperRepoImpl() }

    single <SettingsDataStoreRepo>{ SettingsDataStoreRepoImpl(get()) }
    single <BookmarkManagerRepo>{ BookmarkManagerRepoImpl(get(),get(),get(),get()) }
    viewModel { AuthenticationViewModel(get(),get ()) }
    viewModel { HomeScreenViewModel(get(),get()) }
}
fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(appModule)
}
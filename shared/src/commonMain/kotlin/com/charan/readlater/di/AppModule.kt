package com.charan.readlater.di


import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.BookmarkQueries
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.repository.BackupRepo
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.data.repository.WebScrapperRepo
import com.charan.readlater.data.repository.impl.BackupRepoImpl
import com.charan.readlater.data.repository.impl.BookmarkManagerRepoImpl
import com.charan.readlater.data.repository.impl.BookmarkRepositoryImpl
import com.charan.readlater.data.repository.impl.CategoryRepositoryImpl
import com.charan.readlater.data.repository.impl.SettingsDataStoreRepoImpl
import com.charan.readlater.data.repository.impl.SupabaseRepoImpl
import com.charan.readlater.data.repository.impl.SyncManagerImpl
import com.charan.readlater.data.repository.impl.WebScrapperRepoImpl
import com.charan.readlater.presentation.add_url.AddURLViewModel
import com.charan.readlater.presentation.home.HomeScreenViewModel
import com.charan.readlater.presentation.authentication.AuthenticationViewModel
import com.charan.readlater.presentation.settings.SettingsScreenViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.concurrent.Volatile
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

    single <CategoryRepository>{ CategoryRepositoryImpl(get()) }
    single <WebScrapperRepo>{ WebScrapperRepoImpl() }
    single <BackupRepo>{ BackupRepoImpl(get()) }
    single <SettingsDataStoreRepo>{ SettingsDataStoreRepoImpl(get()) }
    single <BookmarkManagerRepo>{ BookmarkManagerRepoImpl(get(),get(),get(),get(),get()) }
    single <SyncManager>{ SyncManagerImpl(get(),get(),get()) }
    viewModel { AuthenticationViewModel(get(),get ()) }
    viewModel { HomeScreenViewModel(get(),get(),get(),get(),get()) }
    viewModel { SettingsScreenViewModel(get(),get(),get(),get()) }
    viewModel { AddURLViewModel(get(),get(),get()) }
    single <BookmarkRepository>{  BookmarkRepositoryImpl(get()) }
}
@Volatile
private var koinInitialized = false

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    if (koinInitialized) return
    startKoin {
        modules(appModule)
        appDeclaration()
    }
    koinInitialized = true
}

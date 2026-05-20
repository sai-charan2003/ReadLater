package com.charan.readlater.di


import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.repository.SettingsRepository
import com.charan.readlater.data.repository.impl.BookmarkRepositoryImpl
import com.charan.readlater.data.repository.impl.CategoryRepositoryImpl
import com.charan.readlater.data.repository.impl.SettingsRepositoryImpl
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
    single <SettingsRepository>{ SettingsRepositoryImpl(get()) }
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

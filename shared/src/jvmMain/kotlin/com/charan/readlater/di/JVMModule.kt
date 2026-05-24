package com.charan.readlater.di

import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.sync.SyncManager
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import kotlin.concurrent.Volatile

@Module
@Configuration("app")
actual class PlatformModule {
    @Single
    fun provideSqlDriver(databaseFactory: DatabaseFactory): SqlDriver =
        runBlocking { databaseFactory.createDriver() }

    @Single
    fun provideDataStore() = createDataStore()

    @Single
    fun provideSyncManager(
        bookmarkRepository: BookmarkRepository,
        categoryRepository: CategoryRepository,
        supabaseRemoteDataSource: SupabaseRemoteDataSource
    ): SyncManager = SyncManager(bookmarkRepository, categoryRepository, supabaseRemoteDataSource)
}

class KoinInitHelper {
    fun initKoin() {
        initKoin()
    }
}

@Volatile
private var koinInitialized = false

actual fun initKoin(appDeclaration: KoinAppDeclaration) {
    if (koinInitialized) return
    startKoin {
        modules(AppModule().module())
        modules(PlatformModule().module())
        appDeclaration()
    }
    koinInitialized = true
}

package com.charan.readlater.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import com.charan.readlater.data.sync.SyncManager
import com.charan.readlater.data.sync.SyncWork
import kotlinx.coroutines.runBlocking
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.concurrent.Volatile

@Module
@Configuration("app")
actual class PlatformModule {
    @Single
    fun provideSqlDriver(databaseFactory: DatabaseFactory): SqlDriver =
        runBlocking { databaseFactory.createDriver() }

    @Single
    fun provideDataStore(context: Context): DataStore<Preferences> = createDataStore(context = context)

    @Single
    fun provideSyncManager(
        context: Context,
        bookmarkRepository: com.charan.readlater.data.repository.BookmarkRepository,
        categoryRepository: com.charan.readlater.data.repository.CategoryRepository,
        supabaseRemoteDataSource: com.charan.readlater.data.remote.SupabaseRemoteDataSource
    ): SyncManager = SyncManager(context, bookmarkRepository, categoryRepository, supabaseRemoteDataSource)
}


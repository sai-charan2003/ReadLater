package com.charan.readlater.di

import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.ReadLaterDatabase
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import io.github.jan.supabase.SupabaseClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.dsl.KoinAppDeclaration

@Module
@Configuration("app")
@ComponentScan("com.charan.readlater")
class AppModule {

    @Single
    fun provideDatabase(driver: SqlDriver): ReadLaterDatabase = ReadLaterDatabase(driver = driver)

    @Single
    fun provideSupabaseClient(
        readLaterSupabaseClient: ReadLaterSupabaseClient
    ): SupabaseClient = readLaterSupabaseClient.client

    @Single
    fun provideBookmarkQueries(database: ReadLaterDatabase) = database.bookmarkQueries

    @Single
    fun provideCategoryQueries(database: ReadLaterDatabase) = database.categoryQueries
}

@Module
@Configuration("app")
expect class PlatformModule()


@KoinApplication(modules = [AppModule::class, PlatformModule::class])
class App

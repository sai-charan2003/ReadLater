package com.charan.readlater.di

import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import com.charan.readlater.data.sync.SyncManager
import kotlinx.coroutines.runBlocking
import org.koin.core.module.Module
import org.koin.dsl.module

fun iosModule(): Module = module {
    single { DatabaseFactory() }
    single<SqlDriver> { runBlocking { get<DatabaseFactory>().createDriver() } }
    single { createDataStore() }
    single { SyncManager(get(), get(), get()) }
}

class KoinInitHelper() {
    fun initKoin() {
        initKoin {
            modules(iosModule())
        }
    }
}

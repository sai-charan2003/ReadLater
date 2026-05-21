package com.charan.readlater.di

import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import com.charan.readlater.data.sync.SyncWork
import com.charan.readlater.data.sync.SyncManager
import kotlinx.coroutines.runBlocking
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val androidModule= module {
    single { DatabaseFactory(context = get()) }
    single<SqlDriver> { runBlocking { get<DatabaseFactory>().createDriver() } }
    single { createDataStore(context = get()) }
    single { SyncManager(get(), get(), get(), get()) }
    workerOf(::SyncWork)
}

class KoinInitHelper() {
    fun initKoin() {
        initKoin {
            modules(androidModule)
        }
    }
}

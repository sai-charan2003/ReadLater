package com.charan.readlater.di

import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import com.charan.readlater.data.worker.SyncWorker
import kotlinx.coroutines.runBlocking
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val androidModule= module {
    single { DatabaseFactory(context = get()) }
    single<SqlDriver> { runBlocking { get<DatabaseFactory>().createDriver() } }
    single { createDataStore(context = get()) }
    workerOf(::SyncWorker)
}

class KoinInitHelper() {
    fun initKoin() {
        initKoin {
            modules(androidModule)
        }
    }
}

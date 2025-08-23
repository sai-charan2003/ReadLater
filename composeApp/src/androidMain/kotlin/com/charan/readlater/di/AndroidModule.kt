package com.charan.readlater.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val androidModule= module {
    single { DatabaseFactory(context = get()) }
    single<SqlDriver> { runBlocking { get<DatabaseFactory>().createDriver() } }
    single { createDataStore(context = get()) }
}

class KoinInitHelper() {
    fun initKoin() {
        initKoin {
            modules(androidModule)
        }
    }
}

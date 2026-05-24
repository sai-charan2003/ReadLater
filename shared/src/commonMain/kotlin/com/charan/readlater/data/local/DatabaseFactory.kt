package com.charan.readlater.data.local

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.annotation.Singleton

@Singleton
expect class DatabaseFactory {
    suspend fun createDriver(): SqlDriver
}

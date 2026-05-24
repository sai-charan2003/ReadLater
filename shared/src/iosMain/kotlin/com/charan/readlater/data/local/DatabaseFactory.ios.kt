package com.charan.readlater.data.local

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.charan.readlater.ReadLaterDatabase
import org.koin.core.annotation.Singleton

@Singleton
actual class DatabaseFactory {
    actual suspend fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = ReadLaterDatabase.Schema.synchronous(),
            name = "read_later.db"
        )
    }
}

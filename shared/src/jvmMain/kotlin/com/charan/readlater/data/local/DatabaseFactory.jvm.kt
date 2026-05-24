package com.charan.readlater.data.local

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.charan.readlater.ReadLaterDatabase
import org.koin.core.annotation.Singleton
import java.util.Properties

@Singleton
actual class DatabaseFactory {
    actual suspend fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, Properties(),
            ReadLaterDatabase.Schema.synchronous())
        return driver
    }
}

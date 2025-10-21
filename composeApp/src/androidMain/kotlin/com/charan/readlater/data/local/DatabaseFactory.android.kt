package com.charan.readlater.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.charan.readlater.ReadLaterDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory


actual class DatabaseFactory(private val context : Context) {
    actual suspend fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ReadLaterDatabase.Schema.synchronous(),
            context = context,
            name = "read_later.db",
            factory = FrameworkSQLiteOpenHelperFactory()

        )

    }
}
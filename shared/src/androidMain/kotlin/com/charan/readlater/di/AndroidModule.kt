package com.charan.readlater.di

import app.cash.sqldelight.db.SqlDriver
import com.charan.readlater.createDataStore
import com.charan.readlater.data.local.DatabaseFactory
import com.charan.readlater.data.sync.SyncWork
import com.charan.readlater.data.sync.SyncManager
import kotlinx.coroutines.runBlocking
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

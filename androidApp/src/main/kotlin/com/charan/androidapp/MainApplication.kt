package com.charan.androidapp

import android.app.Application
import androidx.work.Configuration
import com.charan.readlater.di.App

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.startKoin
import org.koin.plugin.module.dsl.startKoin

class MainApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()

        startKoin <App>{
            androidContext(this@MainApplication)
            workManagerFactory()
            androidLogger()

        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(KoinWorkerFactory())
            .build()
}


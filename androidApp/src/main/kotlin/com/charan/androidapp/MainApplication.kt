package com.charan.androidapp

import android.app.Application
import com.charan.readlater.di.androidModule
import com.charan.readlater.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            workManagerFactory()
            modules(androidModule)
            androidLogger()

        }
    }

}

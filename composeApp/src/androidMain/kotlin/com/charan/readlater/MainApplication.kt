package com.charan.readlater

import android.app.Application
import com.charan.readlater.di.androidModule
import com.charan.readlater.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            modules(androidModule)
            androidLogger()
        }
    }

}
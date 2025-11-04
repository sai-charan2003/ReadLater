package com.charan.readlater

import androidx.compose.ui.window.ComposeUIViewController
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.data.worker.SyncWorker
import com.charan.readlater.data.worker.registerTask
import com.charan.readlater.di.KoinInitHelper
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController {
    KoinInitHelper().initKoin()
    App()
}

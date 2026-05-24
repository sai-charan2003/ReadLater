package com.charan.readlater

import androidx.compose.ui.window.ComposeUIViewController
import com.charan.readlater.di.App
import org.koin.core.context.startKoin
import org.koin.plugin.module.dsl.startKoin

fun MainViewController() = run {
    startKoin <App>{}
    ComposeUIViewController {
        App()
    }

}

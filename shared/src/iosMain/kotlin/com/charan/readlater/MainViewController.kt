package com.charan.readlater

import androidx.compose.ui.window.ComposeUIViewController
import com.charan.readlater.di.KoinInitHelper

fun MainViewController() = run {
    KoinInitHelper().initKoin()
    ComposeUIViewController {
        App()
    }
}

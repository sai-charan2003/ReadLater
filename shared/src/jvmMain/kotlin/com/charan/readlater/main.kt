package com.charan.readlater

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.charan.readlater.di.KoinInitHelper

fun main() = application {
    KoinInitHelper().initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "ReadLater",
    ) {
        App()
    }
}
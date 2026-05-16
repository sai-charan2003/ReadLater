package com.charan.readlater

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createDataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val file = File(System.getProperty("~/Library/Application Support/ReadLater"), dataStoreFileName)
        file.absolutePath
    }
)
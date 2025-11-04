package com.charan.readlater

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(getSharedURLFromIntent(intent))
        }
    }
}

fun getSharedURLFromIntent(intent : Intent) : String{
    var sharedURL = ""
    if (intent.action == Intent.ACTION_SEND) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            sharedURL = it
        }
    }
    return sharedURL
}
package com.charan.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.charan.readlater.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent(
            content = {
                App(getSharedURLFromIntent(intent))
            }
        )
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

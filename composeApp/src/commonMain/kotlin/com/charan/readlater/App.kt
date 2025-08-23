package com.charan.readlater


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.charan.readlater.presentation.navigation.NavAppHost
import com.charan.readlater.ui.theme.ReadLaterTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = BuildKonfig.GOOGLE_SERVER_ID))
    ReadLaterTheme{
        NavAppHost(
            navHostController = rememberNavController()
        )

    }
}



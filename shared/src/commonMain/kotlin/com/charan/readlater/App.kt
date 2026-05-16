package com.charan.readlater


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.presentation.navigation.NavAppHost
import com.charan.readlater.ui.theme.ReadLaterTheme
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject
import kotlin.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    sharedURL : String = ""

) {
    val settingsDataStoreRepo: SettingsDataStoreRepo = koinInject()
    val syncManager : SyncManager = koinInject()
    var isLoggedIn by rememberSaveable{
        mutableStateOf(true)
    }
    LaunchedEffect(Unit){
        isLoggedIn = settingsDataStoreRepo.getLoginType().first()!=null
        syncManager.syncListener()
    }
    GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = BuildKonfig.GOOGLE_SERVER_ID))
    ReadLaterTheme{
        Surface {
            NavAppHost(
                navHostController = rememberNavController(),
                isLoggedIn = isLoggedIn,
                sharedURL = sharedURL
            )
        }

    }
}



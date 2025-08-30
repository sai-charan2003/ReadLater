package com.charan.readlater.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.readlater.presentation.settings.components.SettingsItem
import com.charan.readlater.presentation.settings.components.SettingsSubHeading
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onPop : () -> Unit,
    onAccountScreenOpen : () -> Unit
) {
    val viewModel = koinViewModel<SettingsScreenViewModel>()
    LaunchedEffect(Unit){
        viewModel.effect.collectLatest {
            when(it){
                SettingsScreenEffeect.NavigateBack -> {
                    onPop()
                }
                SettingsScreenEffeect.NavigateToAccountScreen -> {
                    onAccountScreenOpen()

                }
                is SettingsScreenEffeect.ShowError -> {

                }

                SettingsScreenEffeect.NavigateToLoginScreen -> {

                }
            }
        }
    }

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onEvent(SettingsScreenEvents.OnBackPressed)

                    }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack,null)

                    }


                }
            )
        }
    ) {
        LazyColumn (
            modifier = Modifier.fillMaxSize().padding(it)
        ){
            item {
                SettingsSubHeading(
                    title = "Account"
                )
                SettingsItem(
                    text = "Account Settings",
                    icon = Icons.Outlined.AccountCircle,
                    isClickable = true,
                    onClick = {
                        viewModel.onEvent(SettingsScreenEvents.OnAccountScreenClick)

                    }
                )


            }
        }
    }

}
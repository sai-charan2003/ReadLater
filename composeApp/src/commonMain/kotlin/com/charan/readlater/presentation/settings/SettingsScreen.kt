package com.charan.readlater.presentation.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.charan.readlater.presentation.settings.components.SettingsItem
import com.charan.readlater.presentation.settings.components.SettingsSubHeading
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    viewModel.onEvent(SettingsScreenEvents.OnBackPressed)


                }
            )
        }
    ) {
        LazyColumn (
            modifier = Modifier.padding(it)
        ){
            item {
                SettingsSubHeading(
                    title = "Account"
                )
                SettingsItem(
                    text = "Account Settings",
                    icon = Icons.Rounded.AccountCircle,
                    isClickable = true,
                    onClick = {
                        viewModel.onEvent(SettingsScreenEvents.OnAccountScreenClick)

                    }
                )


            }
        }
    }

}
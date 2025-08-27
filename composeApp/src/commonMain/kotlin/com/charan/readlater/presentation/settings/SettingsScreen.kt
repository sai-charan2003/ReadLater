package com.charan.readlater.presentation.settings

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.charan.readlater.presentation.settings.components.SettingsItem
import com.charan.readlater.presentation.settings.components.SettingsSubHeading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onPop : () -> Unit
) {

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {

                }
            )
        }
    ) {
        LazyColumn {
            item {
                SettingsSubHeading(
                    title = "Account"
                )
                SettingsItem(
                    text = "Account Settings",
                    icon = Icons.Rounded.AccountCircle,
                    isClickable = true,
                    onClick = {

                    }
                )


            }
        }
    }

}
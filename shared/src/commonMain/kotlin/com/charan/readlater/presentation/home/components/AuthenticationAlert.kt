package com.charan.readlater.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable

fun UserAuthenticationAlert(
    onLoginClick : () -> Unit,
    onDismiss : () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(onClick = onLoginClick){
                Text("Login")
            }
        },
        title = {
            Text("Authentication Required")
        },
        text = {
            Text("User Session Expired, Please login to sync your data")
        },
        icon = {
            Icon(Icons.Rounded.Login,null)
        }


    )
}
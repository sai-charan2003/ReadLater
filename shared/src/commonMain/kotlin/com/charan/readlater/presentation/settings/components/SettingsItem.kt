package com.charan.readlater.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsItem(
    text : String,
    icon : ImageVector,
    subHeading : String? = null,
    onClick : () -> Unit ={},
    isClickable : Boolean = false
) {

    ListItem(
        headlineContent = {
            Text(text)
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                null

            )
        },
        supportingContent = {
            if(subHeading != null){
                Text(subHeading)
            }
        },
        modifier = Modifier.fillMaxWidth().clickable(isClickable){
            onClick()
        },

    )

}
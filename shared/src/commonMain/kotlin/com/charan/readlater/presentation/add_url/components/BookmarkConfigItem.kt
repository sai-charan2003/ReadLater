package com.charan.readlater.presentation.add_url.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.charan.readlater.presentation.add_url.AddURLEvents
import com.charan.readlater.ui.theme.IndexItem
import com.charan.readlater.ui.theme.roundedListItemCorners

@Composable
fun BookmarkConfigItem(
    title : String,
    trailingContent : @Composable () -> Unit,
    onClick : () -> Unit,
    index : IndexItem
) {
    Surface(
        shape = roundedListItemCorners(index)
    ) {
        ListItem(
            headlineContent = {
                Text(title)
            },
            trailingContent =
                trailingContent
            ,
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            modifier = Modifier.clickable(true){
                onClick()
            }
        )
    }


}
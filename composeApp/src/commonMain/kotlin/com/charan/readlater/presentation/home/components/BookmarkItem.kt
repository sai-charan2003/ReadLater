package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil3.compose.AsyncImage

@Composable

fun BookmarkItem(
    title : String,
    description : String,
    imageUrl : String,
    onClick : () -> Unit,
    isDue : Boolean = false,
    onContextMenuOpen : () -> Unit,
    onLeftToRightSwipe : () -> Unit,
    onRightToLeftSwipe : () -> Unit,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if(it == SwipeToDismissBoxValue.StartToEnd) {
                onLeftToRightSwipe()
                false
            }
            else if(it == SwipeToDismissBoxValue.EndToStart) {
                onRightToLeftSwipe()
                true
            }
            it != SwipeToDismissBoxValue.StartToEnd
        }
    )
    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        backgroundContent = {
            val progress = swipeToDismissBoxState.progress
            val direction = swipeToDismissBoxState.dismissDirection

            if (direction == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            lerp(Color(0xFFFFEEEE), Color.Red, progress)
                        )
                        ,
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
            }

            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                // you can add something else here (e.g. archive icon with green bg)
            }
        },
        content = {
            ListItem(
                leadingContent = {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(120.dp)
                            .height(71.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                },
                headlineContent = { Text(title) },
                supportingContent = { if (isDue) DueBadge() },
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = { onContextMenuOpen() }
                    )
                }
            )
        }
    )




}
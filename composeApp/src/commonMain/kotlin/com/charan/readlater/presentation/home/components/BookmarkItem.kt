package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil3.compose.AsyncImage

@Composable
fun BookmarkItem(
    title: String,
    description: String,
    imageUrl: String,
    onClick: () -> Unit,
    isDue: Boolean = false,
    onContextMenuOpen: () -> Unit,
    onLeftToRightSwipe: () -> Unit,
    onRightToLeftSwipe: () -> Unit,
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onLeftToRightSwipe()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onRightToLeftSwipe()
                    false
                }
                else -> false
            }
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
                        .background(lerp(Color(0xFFFFEEEE), Color.Red, progress)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                        Text(
                            text = "Delete",
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(lerp(Color(0xFFE8F5E9), Color(0xFF4CAF50), progress)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 24.dp)
                    ) {
                        Icon(
                            imageVector = if (isDue) Icons.Default.Schedule else Icons.Default.CheckCircle,
                            contentDescription = if (isDue) "Mark as Read" else "Set as Due",
                            tint = Color.White
                        )
                        Text(
                            text = if (isDue) "Mark as Read" else "Set as Due",
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        content = {
            ListItem(
                leadingContent = {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(75.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
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

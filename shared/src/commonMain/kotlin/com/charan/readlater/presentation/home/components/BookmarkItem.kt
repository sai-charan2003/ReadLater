package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import coil3.compose.AsyncImage
import com.charan.readlater.presentation.components.StyledList
import com.charan.readlater.presentation.components.StyledList
import com.charan.readlater.ui.theme.IndexItem
import com.charan.readlater.ui.theme.roundedListItemCorners

@Composable
fun BookmarkItem(
    title: String,
    description: String,
    createdAt: String,
    category : String,
    hostUrl : String,
    imageUrl: String,
    onClick: () -> Unit,
    isDue: Boolean = false,
    onContextMenuOpen: () -> Unit,
    onLeftToRightSwipe: () -> Unit,
    onRightToLeftSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    indexItem: IndexItem
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
    StyledList(
        indexItem = indexItem,
        content = {
            SwipeToDismissBox(
                modifier = modifier,
                state = swipeToDismissBoxState,
                enableDismissFromEndToStart = false,
                enableDismissFromStartToEnd = false,
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
                    BookmarkListItem(
                        title = title,
                        hostUrl = hostUrl,
                        imageUrl = imageUrl,
                        onClick = onClick,
                        isDue = isDue,
                        onContextMenuOpen = onContextMenuOpen,
                        indexItem = indexItem,
                        categoryName = category,
                        createdAt = createdAt
                    )
                }
            )

        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BookmarkListItem(
    title: String,
    hostUrl: String,
    imageUrl: String,
    onClick: () -> Unit,
    onContextMenuOpen: () -> Unit,
    modifier: Modifier = Modifier,
    categoryName : String,
    createdAt : String,
    isDue: Boolean = false,
    indexItem: IndexItem
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(hostUrl.isNotEmpty()) {
                        Text(
                            text = hostUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
            if(imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(categoryName.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Rounded.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                if (isDue) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(
                onClick = onContextMenuOpen,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}









package com.charan.readlater.presentation.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.charan.readlater.presentation.home.DrawerItems

@Composable
fun DrawerContent(
    items: List<DrawerItems>,
    selectedIndex: Int,
    onItemClick: (DrawerItems, Int) -> Unit,
    onEdit : (DrawerItems.Category) -> Unit,
    onDelete : (DrawerItems.Category) -> Unit
) {
    var expandedItemIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = Modifier
    ) {
        items(items.size) { index ->
            val item = items[index]
            Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                if (item is DrawerItems.Category) {
                    val categoryItem = item.categoryItem
                    CategoryDrawerItem(
                        title = categoryItem.name,
                        selected = index == selectedIndex,
                        showCustomizeIcons = expandedItemIndex == index,
                        onClick = {
                            onItemClick(item, index)
                        },
                        onLongPress = {
                            expandedItemIndex =
                                if (expandedItemIndex == index) null else index
                        },
                        onEdit ={
                            onEdit(item)
                        },
                        onDelete = {
                            onDelete(item)
                        }
                    )
                } else {
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = index == selectedIndex,
                        onClick = {
                            onItemClick(item, index)
                        },
                        modifier = Modifier.padding(2.dp),
                    )
                }
            }
            if (item == DrawerItems.ReadLater) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = 5.dp, start = 10.dp, end = 10.dp)
                )
            }
        }
    }

}



@Composable
fun CategoryDrawerItem(
    title: String,
    selected: Boolean,
    showCustomizeIcons: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onEdit : () -> Unit = {},
    onDelete : () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            selected = selected,
            onClick = onClick,
            modifier = Modifier
                .semantics { role = Role.Tab }
                .heightIn(min = 56.dp)
                .weight(1f)
                .animateContentSize(
                    spring(
                        stiffness = Spring.StiffnessMediumLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ),
            shape = CircleShape,
            color = NavigationDrawerItemDefaults.colors().containerColor(selected).value,
        ) {
            Row(
                modifier = Modifier
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongPress,
                        interactionSource = remember { MutableInteractionSource() },
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = showCustomizeIcons,
            enter = slideInHorizontally(
                initialOffsetX = { it / 2 },
                animationSpec = spring(
                    stiffness = Spring.StiffnessMediumLow,
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            ),
            exit = fadeOut()+slideOutHorizontally(
                targetOffsetX = { it /2 }
                ,animationSpec = spring(
                stiffness = Spring.StiffnessMediumLow,
                dampingRatio = Spring.DampingRatioMediumBouncy
            ))
        ) {
            Row {
                FilledTonalIconButton(
                    onClick = {
                        onEdit()
                    }
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                }
                FilledTonalIconButton(
                    onClick = {
                        onDelete()
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}


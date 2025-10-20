package com.charan.readlater.presentation.add_url.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.charan.readlater.presentation.home.CategoryItem
import com.charan.readlater.ui.theme.IndexItem
import com.charan.readlater.ui.theme.roundedListItemCorners

@Composable

fun CategoryItem(
    category: CategoryItem,
    onSelect: () -> Unit,
    indexItem: IndexItem
) {
    Surface(
        shape = roundedListItemCorners(indexItem)
    ) {
        Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            RadioButton(
                selected = category.isSelected,
                onClick = {
                    onSelect()
                }

            )

            Text(
                text = category.name
            )
        }
    }
}
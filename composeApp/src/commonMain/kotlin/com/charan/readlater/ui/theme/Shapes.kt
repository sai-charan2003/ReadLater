package com.charan.readlater.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun roundedListItemCorners(
    indexItem: IndexItem,
    topBottomRadius: Dp = 16.dp,
    middleRadius: Dp = 4.dp
): RoundedCornerShape {
    val shape = when (indexItem) {
        IndexItem.FIRST -> RoundedCornerShape(
            topStart = topBottomRadius,
            topEnd = topBottomRadius,
            bottomStart = middleRadius,
            bottomEnd = middleRadius
        )
        IndexItem.LAST -> RoundedCornerShape(
            topStart = middleRadius,
            topEnd = middleRadius,
            bottomStart = topBottomRadius,
            bottomEnd = topBottomRadius
        )

        IndexItem.MIDDLE -> {
            RoundedCornerShape(
                topStart = middleRadius,
                topEnd = middleRadius,
                bottomStart = middleRadius,
                bottomEnd = middleRadius
            )
        }
    }

    return shape
}

enum class IndexItem {
    FIRST,
    LAST,
    MIDDLE
}
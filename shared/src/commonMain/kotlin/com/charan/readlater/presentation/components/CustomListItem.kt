package com.charan.readlater.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.readlater.ui.theme.IndexItem
import com.charan.readlater.ui.theme.roundedListItemCorners

@Composable
fun StyledList(
    indexItem: IndexItem,
    content : @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues = PaddingValues(10.dp)

) {

    Surface(
        shape = roundedListItemCorners(indexItem = indexItem),
        modifier = Modifier.fillMaxWidth().then(modifier),
        color = MaterialTheme.colorScheme.surfaceContainerLowest

    ) {
        Column(
            modifier = Modifier.padding(contentPaddingValues),
            content = content
        )

    }
    Spacer(Modifier.height(2.dp))

}
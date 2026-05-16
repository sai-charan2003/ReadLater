package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DueBadge(
    modifier: Modifier = Modifier,
    label: String = "Due",
    showIcon: Boolean = false
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    modifier = Modifier
                        .size(12.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun DueBadgePreview() {
    MaterialTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            DueBadge()
            DueBadge(
                modifier = Modifier.padding(start = 8.dp),
                showIcon = true
            )
            DueBadge(
                modifier = Modifier.padding(start = 8.dp),
                label = "Overdue"
            )
        }
    }
}
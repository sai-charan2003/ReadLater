package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DueBadge(
    modifier: Modifier = Modifier,
    label: String = "Due"
) {
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .then(modifier),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(50),
        shadowElevation = 2.dp
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun DueBadgePreview() {
        DueBadge()
}
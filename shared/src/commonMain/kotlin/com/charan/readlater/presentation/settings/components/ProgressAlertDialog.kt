package com.charan.readlater.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProgressAlertDialog(
    title: String,
    progress: Float,
    total: Long = 0L,
    current: Long = 0L
) {
    AlertDialog(
        icon = { Icon(Icons.Rounded.ImportExport, contentDescription = null) },
        title = { Text(text = title) },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${(progress * 100).toInt()}% completed")


                    if (total > 0L) {
                        Text(
                            text = "$current / $total",
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                LinearWavyProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        },
        onDismissRequest = { /* prevent dismiss while loading */ },
        confirmButton = {}
    )
}

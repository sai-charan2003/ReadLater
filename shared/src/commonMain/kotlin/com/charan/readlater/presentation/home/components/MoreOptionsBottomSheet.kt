package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MoreOptionsBottomSheet(
    onDismissRequest : () -> Unit,
    sheetState : SheetState,
    onShare : () -> Unit = {},
    onEdit : () -> Unit = {},
    onDelete : () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
//            TextButton(
//                onClick = onShare,
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start
//                ) {
//                    TextButtonBody(
//                        icon = {
//                            Icon(Icons.Rounded.Share,"Share")
//                        },
//                        text = "Share"
//                    )
//                }
//            }

            TextButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()

            ) {
                TextButtonBody(
                    icon = {
                        Icon(Icons.Rounded.Edit,"Edit")
                    },
                    text = "Edit"
                )
            }

            TextButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error

                )
            ) {
                TextButtonBody(
                    icon = {
                        Icon(Icons.Rounded.Delete,"Delete")
                    },
                    text = "Delete"
                )
            }

        }
    }
}

@Composable
fun TextButtonBody(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        icon()
        Text(text, modifier = Modifier.padding(start = 10.dp))
    }
}
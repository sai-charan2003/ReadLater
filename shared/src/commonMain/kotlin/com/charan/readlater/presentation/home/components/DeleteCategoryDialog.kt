package com.charan.readlater.presentation.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DeleteCategoryDialog(
    categoryName: String,
    onConfirmDelete: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Delete Category")
        },
        text = {
            Text(text = "Are you sure you want to delete the category \"$categoryName\"? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDelete()
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}
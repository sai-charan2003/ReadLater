package com.charan.readlater.presentation.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EditCategoryDialog(
    categoryName : String,
    onCategoryNameChange : (String) -> Unit,
    onConfirmEdit : () -> Unit,
    onDismissRequest : () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Edit Category")
        },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { newName ->
                    onCategoryNameChange(newName)
                },
                label = { Text("Category Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmEdit()
                }
            ) {
                Text("Save")
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
package com.charan.readlater.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUrlBottomSheet(
    onDismiss: () -> Unit,
    bottomModelSheetState: SheetState,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    savingURL: Boolean = false,
    url: String,
    isDue: Boolean = false,
    onDueChange: (Boolean) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = bottomModelSheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = url,
                onValueChange = onValueChange,
                label = { Text("Enter URL") },
                modifier = Modifier.fillMaxWidth()
            )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mark As Due")
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = isDue,
                        onCheckedChange = onDueChange
                    )
                }
            }

            HorizontalDivider()

            Button(
                onClick = onSaveClick,
                enabled = !savingURL && url.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                
            ) {
                Text("Add Bookmark")
            }
        }
    }
}
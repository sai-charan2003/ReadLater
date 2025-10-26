package com.charan.readlater.presentation.add_url

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.readlater.presentation.add_url.components.BookmarkConfigItem
import com.charan.readlater.presentation.add_url.components.SelectCategoryBottomSheet
import com.charan.readlater.presentation.home.HomeScreenEvent
import com.charan.readlater.presentation.home.HomeScreenViewModel
import com.charan.readlater.ui.theme.IndexItem
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddURLScreen(
    onBackClick: () -> Unit,
    sharedURL : String = "",
    isEdit : Boolean = false,
    uuid : String = ""

) {
    val viewModel : AddURLViewModel = koinViewModel<AddURLViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val categorySheetState = rememberModalBottomSheetState()
    LaunchedEffect(Unit){
        viewModel.onEvent(AddURLEvents.OnURLChange(sharedURL))
    }
    LaunchedEffect(Unit){
        if(isEdit){
            viewModel.onEvent(AddURLEvents.LoadDataForEdit(uuid))
        }
    }


    LaunchedEffect(Unit){
        viewModel.effects.collectLatest { effects ->
            when(effects){
                AddURLEffects.OnBack -> {
                    onBackClick()
                }
                null -> {}
            }
        }
    }
    if(state.categorySelectBottomSheet){
        SelectCategoryBottomSheet(
            onDismiss = {
                viewModel.onEvent(AddURLEvents.OnCategorySheetDismiss)
            },
            onSelect = { category ->
                viewModel.onEvent(AddURLEvents.OnCategorySelect(category))
            },
            sheetState = categorySheetState,
            allCategories = state.categoryItems,
            onCreateCategory = {
                viewModel.onEvent(AddURLEvents.OnCreateCategoryClick)
            },
            newCategoryName = state.newCategoryName,
            onCategoryValueChange = {
                viewModel.onEvent(AddURLEvents.OnNewCategoryNameChange(it))
            }

        )
    }
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text("Add Bookmark")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            null
                        )
                    }
                }
            )
        }
    ) { innerPadding->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.padding(18.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.bookmarkData.url,
                    onValueChange = {
                        viewModel.onEvent(AddURLEvents.OnURLChange(it))
                    },
                    label = { Text("Enter URL") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.errorMessage.isNotEmpty(),
                    supportingText = {
                        if(state.errorMessage.isNotEmpty()){
                            Text(state.errorMessage)
                        } else {
                            null
                        }
                    }

                )

                Button(
                    onClick = { viewModel.onEvent(AddURLEvents.OnSaveURLClick(isEdit)) },
                    enabled = !state.isLoading && state.bookmarkData.url.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Text(if (isEdit) "Update Bookmark" else "Save Bookmark")
                    AnimatedVisibility(state.isLoading, modifier = Modifier.padding(start = 10.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                BookmarkConfigItem(
                    title = "ReadLater",
                    trailingContent = {
                        Switch(
                            checked = state.bookmarkData.isDue,
                            onCheckedChange = {
                                viewModel.onEvent(AddURLEvents.OnDueButtonClick(it))
                            }
                        )
                    },
                    onClick = {
                        viewModel.onEvent(AddURLEvents.OnDueButtonClick(!state.bookmarkData.isDue))

                    },
                    index = IndexItem.FIRST
                )

                BookmarkConfigItem(
                    title = "Category",
                    trailingContent = {
                        Text(state.selectedCategory.ifEmpty { "" })
                    },
                    onClick = {
                        viewModel.onEvent(AddURLEvents.OnCategorySheetOpen)

                    },
                    index = IndexItem.LAST


                )

            }

        }
    }
}
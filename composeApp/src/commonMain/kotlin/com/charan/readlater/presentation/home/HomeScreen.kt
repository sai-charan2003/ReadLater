package com.charan.readlater.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.readlater.presentation.home.components.AddUrlBottomSheet
import com.charan.readlater.presentation.home.components.BookmarkItem
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val viewModel = koinViewModel <HomeScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val addBookmarkModelSheet = rememberModalBottomSheetState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit){
        viewModel.effect.collectLatest { effect ->
            when(effect){
                is HomeScreenEffect.OpenURLInBrowser -> {
                    uriHandler.openUri(effect.url)

                }
                is HomeScreenEffect.ShowError -> {

                }
            }
        }
    }
    if(state.showAddURLBottomSheet) {
        AddUrlBottomSheet(
            onDismiss = {
                viewModel.onEvent(HomeScreenEvent.OnAddURLBottomSheetChangeState)
            },
            bottomModelSheetState = addBookmarkModelSheet,
            onValueChange = {
                viewModel.onEvent(HomeScreenEvent.OnURLChange(it))
            },
            onSaveClick = {
                viewModel.onEvent(HomeScreenEvent.OnSaveURLClick)
            },
            savingURL = state.savingNewUrl,
            url = state.newUrlState.url,
            isDue = state.newUrlState.isDue,
            onDueChange = {
                viewModel.onEvent(HomeScreenEvent.OnDueChange(it))
            }

        )
    }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Read Later") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.onEvent(HomeScreenEvent.OnAddURLBottomSheetChangeState)
                },
                text = { Text("Add") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add")  }
            )

        },
        floatingActionButtonPosition = FabPosition.End,

    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(
                state.readLaterUiItem.size,
                key = { state.readLaterUiItem[it].id }
            ) {
                val item = state.readLaterUiItem[it]
                BookmarkItem(
                    title = item.title,
                    description = item.description,
                    imageUrl = item.imageUrl,
                    onClick = {
                        viewModel.onEvent(HomeScreenEvent.OnURLOpen(item.url))

                    }

                )
            }

        }
    }

}
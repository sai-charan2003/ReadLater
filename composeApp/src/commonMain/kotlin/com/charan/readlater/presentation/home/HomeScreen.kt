package com.charan.readlater.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navigateToSettings : () -> Unit
) {
    val viewModel = koinViewModel <HomeScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val addBookmarkModelSheet = rememberModalBottomSheetState()
    val scrollSate = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(Unit){
        viewModel.effect.collectLatest { effect ->
            println(effect)
            when(effect){
                is HomeScreenEffect.OpenURLInBrowser -> {
                    uriHandler.openUri(effect.url)

                }
                is HomeScreenEffect.ShowError -> {

                }

                HomeScreenEffect.NavigateToSettings -> {

                    navigateToSettings()
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
            savingURL = state.newUrlState.isSaving,
            url = state.newUrlState.url,
            isDue = state.newUrlState.isDue,
            onDueChange = {
                viewModel.onEvent(HomeScreenEvent.OnDueChange(it))
            },
            error = state.newUrlState.error

        )
    }
    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text("Read Later") },
                scrollBehavior = scrollSate,
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(HomeScreenEvent.OnDropDownClick)

                        }
                    ) {
                        Icon(Icons.Rounded.MoreVert,null)
                    }
                    DropdownMenu(
                        expanded = state.isDropDownVisible,
                        onDismissRequest = {
                            viewModel.onEvent(HomeScreenEvent.OnDropDownClick)
                        }

                    ) {
                        DropdownMenuItem(
                            text = {
                                Text("Settings")
                            },
                            onClick = {
                                viewModel.onEvent(HomeScreenEvent.OnSettingsClick)

                            }
                        )

                    }
                }
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
        PullToRefreshBox(
            isRefreshing = state.isFetchingData,
            onRefresh = {
                viewModel.onEvent(HomeScreenEvent.OnRefresh)
            },
            state = pullToRefreshState,
            modifier = Modifier.padding(it)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()

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

                        },
                        isDue = item.isDue,
                        onLeftToRightSwipe = {

                        },
                        onRightToLeftSwipe = {
                            viewModel.onEvent(HomeScreenEvent.OnDeleteBookmark(item.id))

                        },
                        onContextMenuOpen = {

                        }

                    )
                }

            }
        }
    }

}
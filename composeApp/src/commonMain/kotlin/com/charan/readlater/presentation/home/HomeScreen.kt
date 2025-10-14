package com.charan.readlater.presentation.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.readlater.presentation.home.components.AddUrlBottomSheet
import com.charan.readlater.presentation.home.components.BookmarkItem
import com.charan.readlater.presentation.home.components.ScrollToTop
import com.charan.readlater.presentation.home.components.UserAuthenticationAlert
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navigateToSettings : () -> Unit,
    navigateToLoginScreen : () -> Unit
) {
    val viewModel = koinViewModel <HomeScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val addBookmarkModelSheet = rememberModalBottomSheetState()
    val scrollSate = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val uriHandler = LocalUriHandler.current
    val listState = rememberLazyListState()
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

                HomeScreenEffect.NavigateToAuthenticationScreen -> {
                    navigateToLoginScreen()

                }

                HomeScreenEffect.ScrollToTop -> {
                    listState.animateScrollToItem(0)
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
                viewModel.onEvent(HomeScreenEvent.OnDueButtonClick(it))
            },
            error = state.newUrlState.error

        )
    }

    if(state.showUserNotAuthenticatedPop){
        UserAuthenticationAlert(
            onLoginClick = {
                viewModel.onEvent(HomeScreenEvent.NavigateToLoginScreen)

            },
            onDismiss = {
                viewModel.onEvent(HomeScreenEvent.OnAuthenticatedPopDismiss)
            }
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
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add")  },
                expanded = listState.isScrollingUp()
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
            modifier = Modifier.padding(it).nestedScroll(
                scrollSate.nestedScrollConnection
            )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState

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
                            viewModel.onEvent(
                                HomeScreenEvent.OnDueStatusChange(
                                    item.id.toLong(),
                                    item.isDue
                                )
                            )

                        },
                        onRightToLeftSwipe = {
                            viewModel.onEvent(HomeScreenEvent.OnDeleteBookmark(item.id))

                        },
                        onContextMenuOpen = {

                        }

                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }

            }
        }

    }

}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}
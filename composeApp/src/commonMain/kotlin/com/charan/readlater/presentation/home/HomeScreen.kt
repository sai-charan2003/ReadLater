package com.charan.readlater.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.readlater.presentation.home.components.AddUrlBottomSheet
import com.charan.readlater.presentation.home.components.BookmarkItem
import com.charan.readlater.presentation.home.components.DrawerContent
import com.charan.readlater.presentation.home.components.ScrollToTop
import com.charan.readlater.presentation.home.components.SearchInputField
import com.charan.readlater.presentation.home.components.UserAuthenticationAlert
import com.charan.readlater.ui.theme.IndexItem
import com.charan.readlater.ui.theme.roundedListItemCorners
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
    val appBarScrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val appBarState = rememberSearchBarState()
    val pullToRefreshState = rememberPullToRefreshState()
    val uriHandler = LocalUriHandler.current
    val listState = rememberLazyListState()
    val searchTextFieldState = rememberTextFieldState()
    val inputField = @Composable{
        SearchInputField(
            searchBarState = appBarState,
            textFieldState = searchTextFieldState,
            scope = rememberCoroutineScope()
        )
    }
    LaunchedEffect(searchTextFieldState.text){
        viewModel.onEvent(HomeScreenEvent.OnSearch(searchTextFieldState.text.toString()))
    }
    LaunchedEffect(Unit){
        viewModel.effect.collectLatest { effect ->
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

                HomeScreenEffect.ToggleNavigationDrawer -> {
                    if(drawerState.isClosed){
                        drawerState.open()
                    }else{
                        drawerState.close()
                    }
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
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Read Later",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 10.dp))
                DrawerContent(
                    items = state.navigationDrawerState.drawerItems,
                    selectedIndex = state.navigationDrawerState.selectedItemIndex,
                    onItemClick = { _, index ->
                        viewModel.onEvent(HomeScreenEvent.OnNavigationDrawerItemClick(index))

                    }


                )
            }

        }

    ) {
        Scaffold(
            topBar = {
                AppBarWithSearch(
                    state = appBarState,
                    scrollBehavior = appBarScrollBehavior,
                    inputField = inputField,
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.onEvent(HomeScreenEvent.OnNavigationDrawerClick)
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(HomeScreenEvent.OnDropDownClick)

                            }
                        ) {
                            Icon(Icons.Rounded.MoreVert, null)
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
                ExpandedFullScreenSearchBar(state = appBarState, inputField = inputField) {
                    LazyColumn(


                    ) {
                        items(state.searchItems.size) {
                            val item = state.searchItems[it]
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

                                },
                                indexItem = if(it == 0) IndexItem.FIRST else if (it == state.readLaterUiItem.size -1) IndexItem.LAST else IndexItem.MIDDLE

                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }

                    }

                }

            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.onEvent(HomeScreenEvent.OnAddURLBottomSheetChangeState)
                    },
                    text = { Text("Add") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
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
                modifier = Modifier.padding(it)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().nestedScroll(appBarScrollBehavior.nestedScrollConnection).padding(10.dp),
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

                            },
                            indexItem = if(it == 0) IndexItem.FIRST else if (it == state.readLaterUiItem.size -1) IndexItem.LAST else IndexItem.MIDDLE,


                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }

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
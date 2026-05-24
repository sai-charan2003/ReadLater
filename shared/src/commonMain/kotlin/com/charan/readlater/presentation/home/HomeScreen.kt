package com.charan.readlater.presentation.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.charan.readlater.presentation.home.components.BookmarkItem
import com.charan.readlater.presentation.home.components.DeleteCategoryDialog
import com.charan.readlater.presentation.home.components.DrawerContent
import com.charan.readlater.presentation.home.components.EditCategoryDialog
import com.charan.readlater.presentation.home.components.MoreOptionsBottomSheet
import com.charan.readlater.presentation.home.components.SearchInputField
import com.charan.readlater.presentation.home.components.UserAuthenticationAlert
import com.charan.readlater.utils.DateUtils
import com.charan.readlater.ui.theme.indexItemFor
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navigateToSettings : () -> Unit,
    navigateToLoginScreen : () -> Unit,
    navigateToAddURLScreen: (isEdit: Boolean, id: String) -> Unit
) {
    val viewModel = koinViewModel <HomeScreenViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val moreOptionsBottomSheetState = rememberModalBottomSheetState()
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
    if(state.showMoreOptionBottomSheet){
        MoreOptionsBottomSheet(
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.OnMoreOptionBottomSheetDismiss)

            },
            onEdit = {
                viewModel.onEvent(HomeScreenEvent.OnEdit(state.selectedBookmarkId))

            },
            onShare = {

            },
            onDelete = {
                viewModel.onEvent(HomeScreenEvent.OnDeleteBookmark(state.selectedBookmarkId))

            },
            sheetState = moreOptionsBottomSheetState
        )

    }

    if(state.showDeleteCategoryDialog){
        DeleteCategoryDialog(
            categoryName = state.editCategoryState.categoryName,
            onConfirmDelete = {
                viewModel.onEvent(HomeScreenEvent.OnDeleteCategory)
            },
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.OnToggleDeleteConfirmationDialog(categoryUuid = ""))
            }
        )
    }

    if(state.showEditCategoryDialog){
        EditCategoryDialog(
            categoryName = state.editCategoryState.categoryName,
            onCategoryNameChange = { newName ->
                viewModel.onEvent(HomeScreenEvent.OnCategoryNameChange(newName))
            },
            onDismissRequest = {
                viewModel.onEvent(HomeScreenEvent.ToggleEditCategoryDialog(categoryUuid = ""))
            },
            onConfirmEdit = {
                viewModel.onEvent(HomeScreenEvent.OnEditCategory)
            }
        )
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

                is HomeScreenEffect.NavigateToAddURLScreen -> {
                    navigateToAddURLScreen(effect.isEdit,effect.id)

                }
            }
        }
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

                    },
                    onEdit = {
                        viewModel.onEvent(
                            HomeScreenEvent.ToggleEditCategoryDialog(
                                categoryUuid = it.categoryItem.uuid
                            )
                        )

                    },
                    onDelete = {
                        viewModel.onEvent(
                            HomeScreenEvent.OnToggleDeleteConfirmationDialog(
                                categoryUuid = it.categoryItem.uuid
                            )
                        )

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
                    LazyColumn{
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
                                            item.id,
                                            item.isDue
                                        )
                                    )

                                },
                                onRightToLeftSwipe = {
                                    viewModel.onEvent(HomeScreenEvent.OnDeleteBookmark(item.id))

                                },
                                onContextMenuOpen = {
                                    viewModel.onEvent(HomeScreenEvent.OnMoreItemButtonClick(id = item.id))

                                },
                                indexItem = state.searchItems.indexItemFor(it),
                                hostUrl = item.hostUrl,
                                category = item.categoryName,
                                createdAt = if (item.createdAt.isNotBlank()) {
                                    DateUtils.formatReadableDateFromIso(item.createdAt)
                                } else {
                                    ""
                                }

                            )
                        }

                    }

                }

            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.onEvent(HomeScreenEvent.OnAddURLClick)
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
                        state.bookmarks.size,
                        key = { state.bookmarks[it].id }
                    ) {
                        val item = state.bookmarks[it]
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
                                        item.id,
                                        item.isDue
                                    )
                                )

                            },
                            onRightToLeftSwipe = {
                                viewModel.onEvent(HomeScreenEvent.OnDeleteBookmark(item.id))

                            },
                            onContextMenuOpen = {
                                viewModel.onEvent(HomeScreenEvent.OnMoreItemButtonClick(item.id))

                            },
                            indexItem = state.bookmarks.indexItemFor(it),
                            hostUrl = item.hostUrl,
                            category = item.categoryName,
                            createdAt = if (item.createdAt.isNotBlank()) {
                                DateUtils.formatReadableDateFromIso(item.createdAt)
                            } else {
                                ""
                            }


                        )
                        Spacer(Modifier.height(3.dp))
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
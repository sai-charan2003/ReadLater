package com.charan.readlater.presentation.home

import com.charan.readlater.presentation.models.BookmarkUiModel

data class HomeScreenState (
    val isFetchingData : Boolean = false,
    val bookmarks: List<BookmarkUiModel> = emptyList(),
    val showAddURLBottomSheet : Boolean = false,
    val isDropDownVisible : Boolean = false,
    val selectedTabIndex : Int = 0,
    val showUserNotAuthenticatedPop : Boolean = false,
    val searchItems : List<BookmarkUiModel> = emptyList(),
    val navigationDrawerState: NavigationDrawerState = NavigationDrawerState(),
    val categoryItems: List<CategoryItem> = emptyList(),
    val showMoreOptionBottomSheet : Boolean = false,
    val selectedBookmarkId : String = "",
    val showDeleteCategoryDialog : Boolean = false,
    val showEditCategoryDialog : Boolean = false,
    val editCategoryState: EditCategoryState = EditCategoryState()
)


data class NavigationDrawerState(
    val drawerItems : List<DrawerItems> = emptyList(),
    val drawerOpen : Boolean = false,
    val selectedItemIndex : Int = 0
)

data class CategoryItem(
    val uuid : String = "",
    val name : String = "",
    val itemCount : Int = 0,
    val isSelected : Boolean = false
)

data class EditCategoryState(
    val categoryUUID: String = "",
    val categoryName: String = "",
    val errorMessage: String = ""
)
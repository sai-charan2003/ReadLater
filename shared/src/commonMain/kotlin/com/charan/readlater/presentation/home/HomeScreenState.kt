package com.charan.readlater.presentation.home

data class HomeScreenState (
    val isFetchingData : Boolean = false,
    val readLaterUiItem: List<ReadLaterUiItem> = emptyList(),
    val showAddURLBottomSheet : Boolean = false,
    val isDropDownVisible : Boolean = false,
    val newUrlState: NewUrlState = NewUrlState(),
    val selectedTabIndex : Int = 0,
    val showUserNotAuthenticatedPop : Boolean = false,
    val searchItems : List<ReadLaterUiItem> = emptyList(),
    val navigationDrawerState: NavigationDrawerState = NavigationDrawerState(),
    val categoryItems: List<CategoryItem> = emptyList(),
    val showMoreOptionBottomSheet : Boolean = false,
    val selectedBookmarkUuid : String = "",
    val showDeleteCategoryDialog : Boolean = false,
    val showEditCategoryDialog : Boolean = false,
    val editCategoryState : EditCategoryState = EditCategoryState(),
)

data class NewUrlState(
    val url: String = "",
    val isDue: Boolean = false,
    val isSaving : Boolean = false,
    val error : String = ""
)

data class EditCategoryState(
    val categoryName : String = "",
    val isSaving : Boolean = false,
    val errorMessage : String = "",
    val categoryUuid : String =""
)

data class ReadLaterUiItem(
    val uuid : String = "",
    val title : String = "",
    val description : String = "",
    val imageUrl : String = "",
    val url : String = "",
    val isDue : Boolean = false,
    val categoryUuid : String = "",
    val hostUrl : String = "",
    val categoryName : String = "",
    val createdAt : String = "",
    val formattedDate : String = ""
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
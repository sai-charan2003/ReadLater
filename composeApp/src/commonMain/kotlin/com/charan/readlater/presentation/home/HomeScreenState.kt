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
)

data class NewUrlState(
    val url: String = "",
    val isDue: Boolean = false,
    val isSaving : Boolean = false,
    val error : String = ""
)

data class ReadLaterUiItem(
    val id : String = "",
    val title : String = "",
    val description : String = "",
    val imageUrl : String = "",
    val url : String = "",
    val isDue : Boolean = false
)


data class NavigationDrawerState(
    val drawerItems : List<DrawerItems> = emptyList(),
    val drawerOpen : Boolean = false,
    val selectedItemIndex : Int = 0
)
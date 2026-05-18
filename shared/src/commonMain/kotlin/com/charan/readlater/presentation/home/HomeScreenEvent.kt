package com.charan.readlater.presentation.home

sealed interface HomeScreenEvent {

    data object OnAddURLClick : HomeScreenEvent
    data class OnURLChange(val url : String) : HomeScreenEvent
    data object OnSaveURLClick : HomeScreenEvent
    data class OnURLOpen(val url : String) : HomeScreenEvent

    data class  OnDueButtonClick(val isDue : Boolean) : HomeScreenEvent
    data object OnDropDownClick : HomeScreenEvent
    data object OnSettingsClick : HomeScreenEvent

    data object OnRefresh : HomeScreenEvent

    data class OnDeleteBookmark(val uuid : String) : HomeScreenEvent

    data class  OnTabChange(val index : Int) : HomeScreenEvent

    data class OnDueStatusChange(val uuid : String, val isDue: Boolean  ) : HomeScreenEvent

    data object NavigateToLoginScreen : HomeScreenEvent

    data object OnAuthenticatedPopDismiss : HomeScreenEvent

    data object OnScrollToTopClick : HomeScreenEvent

    data class OnSearch(val text : String) : HomeScreenEvent

    data object OnNavigationDrawerClick : HomeScreenEvent

    data class OnNavigationDrawerItemClick(val index : Int) : HomeScreenEvent

    data class OnMoreItemButtonClick(val uuid : String) : HomeScreenEvent

    data object OnMoreOptionBottomSheetDismiss : HomeScreenEvent

    data class OnEdit(val uuid : String) : HomeScreenEvent

    data class OnToggleDeleteConfirmationDialog(val categoryUuid: String) : HomeScreenEvent

    data class ToggleEditCategoryDialog(val categoryUuid: String) : HomeScreenEvent

    data object OnEditCategory : HomeScreenEvent

    data object OnDeleteCategory : HomeScreenEvent

    data class OnCategoryNameChange(val name : String) : HomeScreenEvent
}
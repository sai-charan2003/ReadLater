package com.charan.readlater.presentation.home

sealed interface HomeScreenEvent {

    data object OnAddURLBottomSheetChangeState : HomeScreenEvent
    data class OnURLChange(val url : String) : HomeScreenEvent
    data object OnSaveURLClick : HomeScreenEvent
    data class OnURLOpen(val url : String) : HomeScreenEvent

    data class  OnDueButtonClick(val isDue : Boolean) : HomeScreenEvent
    data object OnDropDownClick : HomeScreenEvent
    data object OnSettingsClick : HomeScreenEvent

    data object OnRefresh : HomeScreenEvent

    data class OnDeleteBookmark(val id : String) : HomeScreenEvent

    data class OnTabChange(val index : Int) : HomeScreenEvent

    data class OnDueStatusChange(val id : Long, val isDue: Boolean  ) : HomeScreenEvent

    data object NavigateToLoginScreen : HomeScreenEvent

    data object OnAuthenticatedPopDismiss : HomeScreenEvent

    data object OnScrollToTopClick : HomeScreenEvent
}
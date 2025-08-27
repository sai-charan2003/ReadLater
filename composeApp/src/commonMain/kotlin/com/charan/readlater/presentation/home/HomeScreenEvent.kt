package com.charan.readlater.presentation.home

sealed interface HomeScreenEvent {

    data object OnAddURLBottomSheetChangeState : HomeScreenEvent
    data class OnURLChange(val url : String) : HomeScreenEvent
    data object OnSaveURLClick : HomeScreenEvent
    data class OnURLOpen(val url : String) : HomeScreenEvent

    data class  OnDueChange(val isDue : Boolean) : HomeScreenEvent
    data object OnDropDownClick : HomeScreenEvent
    data object OnSettingsClick : HomeScreenEvent
}
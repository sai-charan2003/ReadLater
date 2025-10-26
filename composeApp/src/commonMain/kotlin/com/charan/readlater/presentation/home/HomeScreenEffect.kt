package com.charan.readlater.presentation.home

sealed interface HomeScreenEffect {

    data class ShowError(val message : String) : HomeScreenEffect

    data class OpenURLInBrowser(val url : String) : HomeScreenEffect

    data object NavigateToSettings : HomeScreenEffect

    data object NavigateToAuthenticationScreen : HomeScreenEffect

    data object ScrollToTop : HomeScreenEffect

    object ToggleNavigationDrawer : HomeScreenEffect

    data class NavigateToAddURLScreen (val isEdit : Boolean,val uuid : String): HomeScreenEffect



}
package com.charan.readlater.presentation.home

sealed interface HomeScreenEffect {

    data class ShowError(val message : String) : HomeScreenEffect

    data class OpenURLInBrowser(val url : String) : HomeScreenEffect



}
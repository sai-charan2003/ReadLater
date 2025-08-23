package com.charan.readlater.presentation.authentication

sealed interface AuthenticationScreenEffect {
    object NavigateToHome : AuthenticationScreenEffect
    data class ShowError(val message : String) : AuthenticationScreenEffect
}

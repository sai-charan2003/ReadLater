package com.charan.readlater.presentation.authentication

sealed interface AuthenticationEvent {
    data class OnGoogleSignInKey(val token : String) : AuthenticationEvent
    data object OnNoAccountLogin : AuthenticationEvent
}
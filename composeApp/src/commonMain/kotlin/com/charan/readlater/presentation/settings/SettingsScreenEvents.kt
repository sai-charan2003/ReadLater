package com.charan.readlater.presentation.settings

sealed class SettingsScreenEvents {
    object OnBackPressed : SettingsScreenEvents()
    object OnAccountScreenClick : SettingsScreenEvents()
    object OnSignOutClick : SettingsScreenEvents()
    object OnSignInClick : SettingsScreenEvents()
}
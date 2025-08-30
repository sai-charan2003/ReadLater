package com.charan.readlater.presentation.settings

sealed class SettingsScreenEffeect {
    data class ShowError(val message: String) : SettingsScreenEffeect()
    object NavigateToAccountScreen : SettingsScreenEffeect()
    object NavigateBack : SettingsScreenEffeect()
}
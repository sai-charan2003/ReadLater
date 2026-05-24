package com.charan.readlater.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeScreenNav

@Serializable
data object AuthenticationScreenNav

@Serializable
data object SettingsScreenNav

@Serializable
data object AccountScreenNav

@Serializable
data class AddURLScreenNav(val url: String = "",val isEdit : Boolean = false, val id : String = "")
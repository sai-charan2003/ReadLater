package com.charan.readlater.presentation.settings

import com.charan.readlater.data.remote.model.UserDetails


data class SettingsScreenState(
    val userDetails : UserDetails = UserDetails(),
    val isLoggedIn: Boolean = false,
    val isSignOutLoading : Boolean = false,
    val showLogoutDialog : Boolean = false,
    val showProgressDialog : Boolean = false,
    val importProgress: ImportProgress = ImportProgress()
)

data class ImportProgress(
    val isImporting : Boolean = false,
    val progress : Float = 0f,
    val error : String= "",
    val totalItems : Long = 0L,
    val importedItems : Long = 0L
)
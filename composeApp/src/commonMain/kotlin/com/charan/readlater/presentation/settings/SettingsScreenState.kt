package com.charan.readlater.presentation.settings

import com.charan.readlater.data.remote.model.UserDetails


data class SettingsScreenState(
    val userDetails : UserDetails = UserDetails()
)
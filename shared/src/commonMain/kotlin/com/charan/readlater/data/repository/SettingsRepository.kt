package com.charan.readlater.data.repository

import com.charan.readlater.data.local.enums.LoginTypeEnum
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun getLoginType(): Flow<LoginTypeEnum?>

    suspend fun updateLoginType(type: LoginTypeEnum)
}
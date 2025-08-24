package com.charan.readlater.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStoreRepoImpl(
    private val dataStore : DataStore<Preferences>
) : SettingsDataStoreRepo {

    companion object {
        private val loginType  = stringPreferencesKey("login_type")

    }
    override suspend fun getLoginType(): Flow<LoginTypeEnum?> = dataStore.data.map{ preferences ->
        preferences[loginType]?.let { LoginTypeEnum.valueOf(it) }


    }

    override suspend fun updateLoginType(type: LoginTypeEnum) {
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[loginType] = type.name
            }
        }
    }


}
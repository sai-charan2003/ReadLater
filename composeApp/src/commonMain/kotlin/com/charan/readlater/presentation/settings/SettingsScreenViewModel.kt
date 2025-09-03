package com.charan.readlater.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.remote.model.UserDetails
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val supabaseRepo: SupabaseRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo,
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsScreenEffeect>()
    val effect = _effect.asSharedFlow()
    init {
        isLoggedIn()
        getUserDetails()
    }

    private fun isLoggedIn() = viewModelScope.launch {
        settingsDataStoreRepo.getLoginType().collectLatest {
            _state.update { state->
                state.copy(
                    isLoggedIn = it == LoginTypeEnum.GOOGLE
                )
            }
        }

    }

    private fun getUserDetails() = viewModelScope.launch{
        supabaseRepo.getAuthorizedUserDetails().collectLatest { processState->
            when(processState){
                is ProcessState.Error -> {}

                ProcessState.Loading -> {}
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    val userDetails = processState.data as UserDetails
                    _state.update { state->
                        state.copy(
                            userDetails = userDetails
                        )
                    }
                }
            }

        }



    }

    fun onEvent(event : SettingsScreenEvents) = viewModelScope.launch {
        when (event) {
            SettingsScreenEvents.OnAccountScreenClick -> {
                _effect.emit(SettingsScreenEffeect.NavigateToAccountScreen)

            }
            SettingsScreenEvents.OnBackPressed -> {
                _effect.emit(SettingsScreenEffeect.NavigateBack)

            }
            SettingsScreenEvents.OnSignOutClick -> {
                _state.update { it.copy(showLogoutDialog = !it.showLogoutDialog) }


            }

            SettingsScreenEvents.OnSignInClick -> {
                _effect.emit(SettingsScreenEffeect.NavigateToLoginScreen)
            }

            SettingsScreenEvents.OnConfirmSignOutClick -> {
                signOutUser()
                _state.update { it.copy(showLogoutDialog = false) }
            }
        }
    }

    private fun signOutUser() = viewModelScope.launch {
        supabaseRepo.signOutUser().collectLatest { state ->
            when(state){
                is ProcessState.Error -> {
                    _state.update { it.copy(isSignOutLoading = false) }

                }
                ProcessState.Loading -> {
                    _state.update { it.copy(isSignOutLoading = true) }

                }
                ProcessState.NotDetermined ->{

                }

                is ProcessState.Success<*> -> {
                    _state.update { it.copy(isSignOutLoading = false) }
                    settingsDataStoreRepo.updateLoginType(LoginTypeEnum.NO_ACCOUNT)
                    readLaterDataSourceRepo.clearAllData()

                }
            }
        }
    }

}
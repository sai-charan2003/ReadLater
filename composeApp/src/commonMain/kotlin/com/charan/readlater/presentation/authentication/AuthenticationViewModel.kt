package com.charan.readlater.presentation.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.remote.ReadLaterSupabaseClient
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val supabaseRepo: SupabaseRepo,
    private val settingsDataStoreRepo: SettingsDataStoreRepo
) : ViewModel() {

    private val _userAuthenticationStatus = MutableStateFlow(AuthenticationScreenState())

    val state = _userAuthenticationStatus.asStateFlow()

    private val _authenticationScreenEffect = MutableSharedFlow<AuthenticationScreenEffect>()
    val authenticationScreenEffect = _authenticationScreenEffect.asSharedFlow()

    private fun authorizeUser(token : String) = viewModelScope.launch{
        supabaseRepo.authorizeUser(token).collectLatest {
            when(it){
                is ProcessState.Error -> {
                    _authenticationScreenEffect.emit(AuthenticationScreenEffect.ShowError(it.exception))
                }
                ProcessState.Loading -> {
                    _userAuthenticationStatus.update { state->
                        state.copy(
                            isAuthenticating = true
                        )
                    }

                }
                ProcessState.NotDetermined -> {

                }
                is ProcessState.Success<*> -> {
                    authenticationStatus()
                }
            }
        }


    }

    fun onEvent(event: AuthenticationEvent) = viewModelScope.launch(Dispatchers.IO) {
        when(event){
            is AuthenticationEvent.OnGoogleSignInKey -> {
                authorizeUser(event.token)
            }
            AuthenticationEvent.OnNoAccountLogin -> {
                noAccountLogin()

            }
        }
    }

    private fun authenticationStatus() = viewModelScope.launch(Dispatchers.IO) {
        supabaseRepo.authenticationStatus().collectLatest {
            when(it){
                is ProcessState.Success -> {
                    onAuthenticationSuccess()
                }
                ProcessState.Loading -> {
                    _userAuthenticationStatus.update { state->
                        state.copy(
                            isAuthenticating = true
                        )
                    }
                }
                is ProcessState.Error -> {
                    _authenticationScreenEffect.emit(AuthenticationScreenEffect.ShowError(it.exception))
                    _userAuthenticationStatus.update { state->
                        state.copy(
                            isAuthenticating = false,
                            isAuthenticated = false
                        )
                    }
                }

                ProcessState.NotDetermined -> {

                }
            }
        }

    }

    private fun onAuthenticationSuccess() = viewModelScope.launch(Dispatchers.IO) {
        _userAuthenticationStatus.update { state->
            state.copy(
                isAuthenticating = false,
                isAuthenticated = true
            )
        }
        _authenticationScreenEffect.emit(AuthenticationScreenEffect.NavigateToHome)
        settingsDataStoreRepo.updateLoginType(LoginTypeEnum.GOOGLE)
    }

    private fun noAccountLogin() = viewModelScope.launch {
        _userAuthenticationStatus.update {
            it.copy(
                isAuthenticating = false,
                isAuthenticated = true
            )
        }
        _authenticationScreenEffect.emit(AuthenticationScreenEffect.NavigateToHome)
        settingsDataStoreRepo.updateLoginType(LoginTypeEnum.NO_ACCOUNT)
    }


}
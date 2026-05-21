package com.charan.readlater.presentation.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.repository.AuthenticationRepository
import com.charan.readlater.data.repository.SettingsRepository
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    init {
        shouldShowLoginWithNoAccount()
    }

    private val _userAuthenticationStatus = MutableStateFlow(AuthenticationScreenState())

    val state = _userAuthenticationStatus.asStateFlow()

    private val _authenticationScreenEffect = MutableSharedFlow<AuthenticationScreenEffect>()
    val authenticationScreenEffect = _authenticationScreenEffect.asSharedFlow()

    private fun authorizeUser(token : String) = viewModelScope.launch{
        authenticationRepository.authorizeUser(token).let {
            when(it){
                is ProcessState.Error -> {
                    _authenticationScreenEffect.emit(AuthenticationScreenEffect.ShowError(it.exception))
                }
                is ProcessState.Loading -> {
                    _userAuthenticationStatus.update { state->
                        state.copy(
                            isAuthenticating = true
                        )
                    }

                }
                ProcessState.NotDetermined -> {

                }
                is ProcessState.Success<*> -> {
                    onAuthenticationSuccess()
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

            AuthenticationEvent.OnBackPressed -> {
                _authenticationScreenEffect.emit(AuthenticationScreenEffect.NavigateBack)
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
        settingsRepository.updateLoginType(LoginTypeEnum.GOOGLE)
    }

    private fun noAccountLogin() = viewModelScope.launch {
        _userAuthenticationStatus.update {
            it.copy(
                isAuthenticating = false,
                isAuthenticated = true
            )
        }
        _authenticationScreenEffect.emit(AuthenticationScreenEffect.NavigateToHome)
        settingsRepository.updateLoginType(LoginTypeEnum.NO_ACCOUNT)
    }

    private fun shouldShowLoginWithNoAccount() = viewModelScope.launch {
        val login = settingsRepository.getLoginType().first()
        _userAuthenticationStatus.update {
            it.copy(
                showLoginWithNoAccount = login != LoginTypeEnum.NO_ACCOUNT
            )
        }
    }


}
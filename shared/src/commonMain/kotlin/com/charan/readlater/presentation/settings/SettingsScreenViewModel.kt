package com.charan.readlater.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.backup.BackupManager
import com.charan.readlater.data.repository.AuthenticationRepository
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.repository.SettingsRepository
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsScreenViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val backupManager: BackupManager
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
        settingsRepository.getLoginType().collectLatest {
            _state.update { state->
                state.copy(
                    isLoggedIn = it == LoginTypeEnum.GOOGLE
                )
            }
        }

    }

    private fun getUserDetails() = viewModelScope.launch{
        val accountInfo = authenticationRepository.getUserDetails()
        accountInfo?.let {
            _state.update { state ->
                state.copy(accountInfo = it)
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

            SettingsScreenEvents.OnImportClick -> {
                _effect.emit(SettingsScreenEffeect.OpenFilePicker)

            }

            is SettingsScreenEvents.OnFilePickerResult -> {
                    importFromFile(event.path)
            }
        }
    }

    private fun importFromFile(path : String) =viewModelScope.launch{

        backupManager.importFromFile(path).collectLatest { state->
            when(state){
                is ProcessState.Error -> {
                    _state.update { it.copy(importProgress = ImportProgress(error = state.exception), showProgressDialog = false) }


                }
                is ProcessState.Loading -> {

                    _state.update { it.copy(importProgress = ImportProgress(isImporting = true, progress = state.progress, totalItems = state.total, importedItems = state.current), showProgressDialog = true) }

                }
                is ProcessState.Success<*> -> {
                    _state.update { it.copy(importProgress = ImportProgress(isImporting = false, progress = 1f), showProgressDialog = false) }
                }

                ProcessState.NotDetermined -> {

                }
            }

        }

    }

    private fun signOutUser() = viewModelScope.launch {
        when (val state = authenticationRepository.signOutUser()) {
            is ProcessState.Error -> {
                _state.update { it.copy(isSignOutLoading = false) }
                _effect.emit(SettingsScreenEffeect.ShowError(state.exception))
            }
            is ProcessState.Loading -> {
                _state.update { it.copy(isSignOutLoading = true) }
            }
            ProcessState.NotDetermined -> Unit
            is ProcessState.Success<*> -> {
                _state.update { it.copy(isSignOutLoading = false) }
                settingsRepository.updateLoginType(LoginTypeEnum.NO_ACCOUNT)
                bookmarkRepository.clearAllBookmarks()
                categoryRepository.clearAllCategories()
            }
        }
    }

}

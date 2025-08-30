package com.charan.readlater.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.remote.model.UserDetails
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
    private val supabaseRepo: SupabaseRepo
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsScreenEffeect>()
    val effect = _effect.asSharedFlow()
    init {
        getUserDetails()
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


            }
        }
    }

}
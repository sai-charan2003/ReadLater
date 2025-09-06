package com.charan.readlater.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.mappers.toReadLaterUiItem
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.data.repository.impl.SupabaseRepoImpl
import com.charan.readlater.presentation.home.HomeScreenEffect.*
import com.charan.readlater.utils.ProcessState
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val supabaseRepoImpl: SupabaseRepo,
    private val bookmarkManagerRepo: BookmarkManagerRepo,
    private val syncManager: SyncManager
) : ViewModel() {
    init {
        loadSession()
        getAllBookmarks()
    }

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeScreenEffect>()
    val effect = _effect.asSharedFlow()


    private fun loadSession() = viewModelScope.launch {
        val session = async { supabaseRepoImpl.loadSession() }
        session.await()
        fetchData()
    }

    private fun getAllBookmarks()= viewModelScope.launch{
        readLaterDataSourceRepo.getAllItems().collectLatest { items->
            _state.update { state->
                state.copy(
                    readLaterUiItem = items.toReadLaterUiItem()
                )
            }

        }
    }

    fun onEvent(event : HomeScreenEvent) = viewModelScope.launch {
        when(event){

            HomeScreenEvent.OnAddURLBottomSheetChangeState -> {
                _state.update { state->
                    state.copy(
                        showAddURLBottomSheet = !state.showAddURLBottomSheet
                    )
                }
                resetNewURLState()
            }
            HomeScreenEvent.OnSaveURLClick -> {
                val url = state.value.newUrlState
                saveNewURL(url)
            }
            is HomeScreenEvent.OnURLChange -> {
                _state.update { state->
                    state.copy(
                        newUrlState = state.newUrlState.copy(url = event.url, error = "")
                    )
                }
            }

            is HomeScreenEvent.OnURLOpen -> {
                _effect.emit(OpenURLInBrowser(event.url))
            }

            is HomeScreenEvent.OnDueChange -> {
                _state.update { state->
                    state.copy(
                        newUrlState = state.newUrlState.copy(isDue = event.isDue)
                    )
                }
            }

            HomeScreenEvent.OnDropDownClick -> {
                _state.update {
                    it.copy(
                        isDropDownVisible = !it.isDropDownVisible
                    )
                }
            }

            HomeScreenEvent.OnSettingsClick -> {
                _state.update {
                    it.copy(
                        isDropDownVisible = false
                    )
                }
                _effect.emit(NavigateToSettings)

            }

            HomeScreenEvent.OnRefresh -> {
                fetchData()
            }
        }
    }

    private fun saveNewURL(url : NewUrlState) = viewModelScope.launch {
        bookmarkManagerRepo.addBookmark(url.url,url.isDue).collectLatest {
            when(it){
                is ProcessState.Error -> {
                    _state.update { state->
                        state.copy(
                            newUrlState = state.newUrlState.copy(isSaving = false,error =  it.exception)
                        )
                    }

                }
                ProcessState.Loading -> {
                    _state.update { state->
                        state.copy(
                            newUrlState = state.newUrlState.copy(isSaving = true)
                        )
                    }

                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    _state.update { state->
                        state.copy(
                            showAddURLBottomSheet = false,
                        )
                    }
                    resetNewURLState()
                }
            }
        }
    }

    private fun resetNewURLState()= viewModelScope.launch {
        _state.update {
            it.copy(
                newUrlState = NewUrlState()
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun fetchData() = viewModelScope.launch {
        supabaseRepoImpl.authenticationStatus()
            .flatMapLatest { authenticationStatus ->
                when (authenticationStatus) {
                    is ProcessState.Error -> {
                        _effect.tryEmit(ShowError(authenticationStatus.exception))
                        emptyFlow()
                    }

                    ProcessState.Loading -> {
                        _state.update { it.copy(isFetchingData = true) }
                        emptyFlow()
                    }

                    is ProcessState.Success<*> -> syncManager.fetchAndUpdate()

                    else -> emptyFlow()
                }
            }
            .collectLatest { syncState ->
                when (syncState) {
                    is ProcessState.Error -> {
                        _state.update { it.copy(isFetchingData = false) }
                        _effect.tryEmit(ShowError(syncState.exception))
                    }

                    ProcessState.Loading -> {
                        _state.update { it.copy(isFetchingData = true) }
                    }

                    is ProcessState.Success<*> -> {
                        _state.update { it.copy(isFetchingData = false) }
                    }

                    ProcessState.NotDetermined -> {}
                }
            }
    }

}
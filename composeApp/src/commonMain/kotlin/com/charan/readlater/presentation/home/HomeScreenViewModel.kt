package com.charan.readlater.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.mappers.toReadLaterUiItem
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.presentation.home.HomeScreenEffect.*
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val bookmarkManagerRepo: BookmarkManagerRepo
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeScreenEffect>()
    val effect = _effect.asSharedFlow()

    init {
        getAllBookmarks()
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
            }
            HomeScreenEvent.OnSaveURLClick -> {
                val url = state.value.newUrlState.url
                saveNewURL(url)
            }
            is HomeScreenEvent.OnURLChange -> {
                _state.update { state->
                    state.copy(
                        newUrlState = state.newUrlState.copy(url = event.url)
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
        }
    }

    private fun saveNewURL(url : String) = viewModelScope.launch {
        bookmarkManagerRepo.addBookmark(url).collectLatest {
            println(url)
            when(it){
                is ProcessState.Error -> {

                }
                ProcessState.Loading -> {
                    _state.update { state->
                        state.copy(
                            savingNewUrl = true
                        )
                    }

                }
                ProcessState.NotDetermined -> {}
                is ProcessState.Success<*> -> {
                    _state.update {
                        it.copy(
                            showAddURLBottomSheet = false
                        )
                    }
                }
            }
        }
    }

}
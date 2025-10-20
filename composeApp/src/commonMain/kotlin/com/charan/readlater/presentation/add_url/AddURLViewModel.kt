package com.charan.readlater.presentation.add_url

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.CategoryEntity
import com.charan.readlater.data.mappers.toCategoryUIList
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.presentation.home.CategoryItem
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddURLViewModel(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val supabaseRepoImpl: SupabaseRepo,
    private val bookmarkManagerRepo: BookmarkManagerRepo,
    private val syncManager: SyncManager,
    private val settingsDataSourceRepo: SettingsDataStoreRepo
) : ViewModel() {
    private val _state = MutableStateFlow(AddURLState())
    val state = _state.asStateFlow()

    private val _effects = MutableStateFlow<AddURLEffects?>(null)
    val effects = _effects.asSharedFlow()

    init {
        fetchCategories()
    }


    fun onEvent(event : AddURLEvents){
        when(event) {
            is AddURLEvents.OnDueButtonClick -> {
                _state.update { state->
                    state.copy(
                        isDue = event.isDue
                    )
                }

            }
            AddURLEvents.OnSaveURLClick -> {

                saveURL(_state.value.url, _state.value.isDue, _state.value.selectedCategoryUUID)
            }
            is AddURLEvents.OnURLChange ->{
                _state.update { state->
                    state.copy(
                        url = event.url
                    )
                }
            }

            AddURLEvents.OnCategorySheetOpen -> {
                _state.update { state->
                    state.copy(
                        categorySelectBottomSheet = true
                    )
                }
            }

            is AddURLEvents.OnCategorySelect -> {
                onCategorySelected(event.category)

            }
            AddURLEvents.OnCategorySheetDismiss -> {
                _state.update { state->
                    state.copy(
                        categorySelectBottomSheet = false
                    )
                }
            }

            AddURLEvents.OnCreateCategoryClick -> {
                createCategory(_state.value.newCategoryName)

            }
            is AddURLEvents.OnNewCategoryNameChange -> {
                _state.update { state->
                    state.copy(
                        newCategoryName = event.name
                    )
                }
            }
        }
    }

    private fun saveURL(url : String, isDue : Boolean,categoryUUID : String)= viewModelScope.launch{
        bookmarkManagerRepo.addBookmark(url ,isDue,categoryUUID).collectLatest {
            when(it){
                is ProcessState.Error -> {
                    _state.update { state->
                        state.copy(
                            isLoading = false,
                            errorMessage = it.exception ?: "An unexpected error occurred"
                        )
                    }

                }
                is ProcessState.Loading -> {
                    _state.update { state->
                        state.copy(
                            isLoading = true,
                            errorMessage = ""
                        )
                    }
                }
                ProcessState.NotDetermined -> {

                }
                is ProcessState.Success<*> -> {
                    _state.update { state->
                        state.copy(
                            isLoading = false,
                            errorMessage = ""
                        )
                    }
                    _effects.emit(AddURLEffects.OnBack)
                }
            }
        }
    }

    private fun onCategorySelected( categoryItem: CategoryItem) {
        _state.update {
            it.copy(
                selectedCategory = categoryItem.name,
                selectedCategoryUUID = categoryItem.uuid,
                categorySelectBottomSheet = false,
                categoryItems = it.categoryItems.map { item->
                    item.copy(
                        isSelected = item.uuid == categoryItem.uuid
                    )
                }
            )
        }

    }

    private fun fetchCategories() = viewModelScope.launch {
        readLaterDataSourceRepo.getAllCategories().collectLatest { categoryEntities ->
            _state.update {
                it.copy(
                    categoryItems = categoryEntities.toCategoryUIList()
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun createCategory(name : String) = viewModelScope.launch {
        val uuid = Uuid.random().toString()
        bookmarkManagerRepo.createCategory(name,uuid).collectLatest { state->
            println(state)
            when(state){
                is ProcessState.Error -> {
                    _state.update {
                        it.copy(
                            errorMessage = state.exception ?: "An unexpected error occurred"
                        )
                    }
                }
                is ProcessState.Loading -> {
                    _state.update {
                        it.copy(
                            errorMessage = ""
                        )
                    }
                }
                ProcessState.NotDetermined -> TODO()
                is ProcessState.Success<*> -> {
                    _state.update {
                        it.copy(
                            newCategoryName = "",
                            categorySelectBottomSheet = false,
                            selectedCategory = name,
                            selectedCategoryUUID = uuid
                        )
                    }

                }
            }
        }

    }
}
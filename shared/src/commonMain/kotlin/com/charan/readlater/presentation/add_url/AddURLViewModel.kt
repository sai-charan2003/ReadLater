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
                        bookmarkData = state.bookmarkData.copy(
                            isDue = !state.bookmarkData.isDue
                        )
                    )
                }

            }
            is AddURLEvents.OnSaveURLClick -> {
                if(event.isEdit){
                    updateBookmark()
                } else{
                    saveURL()
                }
            }
            is AddURLEvents.OnURLChange ->{
                _state.update { state->
                    state.copy(
                        bookmarkData = state.bookmarkData.copy(
                            url = event.url
                        )
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

            is AddURLEvents.LoadDataForEdit -> {
                loadDataForEdit(event.uuid)

            }
        }
    }

    private fun loadDataForEdit(id : String ) = viewModelScope.launch {
        val bookmark = readLaterDataSourceRepo.getBookmarkByUUID(id)
        _state.update { state->
            state.copy(
                bookmarkData = state.bookmarkData.copy(
                    url = bookmark?.url ?: "",
                    isDue = bookmark?.is_due ?: false,
                    categoryUUID = bookmark?.category_uuid ?: "",
                ),
                selectedCategory = readLaterDataSourceRepo.getCategoryByUUID(bookmark?.category_uuid ?: "")?.name ?: "",
                editUUID = id
            )

        }
    }

    private fun updateBookmark() = viewModelScope.launch {
        bookmarkManagerRepo.updateBookmark(_state.value.bookmarkData,_state.value.editUUID).collectLatest { state->
            when(state){
                is ProcessState.Error -> {
                    _state.update { state->
                        state.copy(
                            isLoading = false,
                            errorMessage = state.errorMessage
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

    private fun saveURL()= viewModelScope.launch{
        bookmarkManagerRepo.addBookmark(_state.value.bookmarkData).collectLatest {
            when(it){
                is ProcessState.Error -> {
                    _state.update { state->
                        state.copy(
                            isLoading = false,
                            errorMessage = it.exception
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
                categorySelectBottomSheet = false,
                categoryItems = it.categoryItems.map { item->
                    item.copy(
                        isSelected = item.uuid == categoryItem.uuid
                    )
                },
                bookmarkData = it.bookmarkData.copy(
                    categoryUUID = categoryItem.uuid
                )
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
                            bookmarkData = it.bookmarkData.copy(
                                categoryUUID = uuid
                            )
                        )
                    }

                }
            }
        }

    }
}
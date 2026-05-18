package com.charan.readlater.presentation.add_url

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.charan.readlater.Category
import com.charan.readlater.data.mappers.toCategoryUIList
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.presentation.mapper.toBookmark
import com.charan.readlater.presentation.mapper.toBookmarkUiModel
import com.charan.readlater.presentation.mapper.toCategoryUiModel
import com.charan.readlater.presentation.models.CategoryUiModel
import com.charan.readlater.presentation.navigation.AddURLScreenNav
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddURLViewModel(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val bookmarkManagerRepo: BookmarkManagerRepo,
    private val savedState : SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(AddURLState())
    val state = _state.asStateFlow()

    private val _effects = MutableStateFlow<AddURLEffects?>(null)
    val effects = _effects.asSharedFlow()

    private val arguments = savedState.toRoute<AddURLScreenNav>()

    init {
        if(arguments.id.isNotEmpty()){
            loadDataForEdit(arguments.id)
        }

        if(arguments.url.isNotEmpty()){
            handleUrlChange(arguments.url)

        }
        fetchCategories()
    }


    fun onEvent(event : AddURLEvents){
        when(event) {
            is AddURLEvents.OnDueButtonClick -> {
                handleDueChange(event.isDue)

            }
            is AddURLEvents.OnSaveURLClick -> {
                handleSaveClick(event.isEdit)
            }
            is AddURLEvents.OnURLChange ->{
                handleUrlChange(event.url)
            }

            AddURLEvents.OnCategorySheetToggle -> {
                handleCategorySheetToggle()

            }

            is AddURLEvents.OnCategorySelect -> {
                handleCategorySelection(event.category)

            }

            AddURLEvents.OnCreateCategoryClick -> {
                createCategory(_state.value.newCategoryName)

            }
            is AddURLEvents.OnNewCategoryNameChange -> {
                handleNewCategoryNameChange(event.name)
            }
        }
    }

    private fun handleDueChange(isDue : Boolean){
        _state.update { state->
            state.copy(
                bookmarkData = state.bookmarkData.copy(
                    isDue = isDue
                )
            )
        }
    }

    private fun handleSaveClick(isEdit : Boolean){
        saveURL()
    }

    private fun handleCategorySheetToggle(){
        _state.update { state->
            state.copy(
                categorySelectBottomSheet = !state.categorySelectBottomSheet
            )
        }
    }

    private fun handleNewCategoryNameChange(name : String){
        _state.update { state->
            state.copy(
                newCategoryName = name
            )
        }
    }

    private fun loadDataForEdit(id : String ) = viewModelScope.launch {
        val bookmark = readLaterDataSourceRepo.getBookmarkWithCategoryById(id)
        _state.update { state->
            state.copy(
                bookmarkData = bookmark.toBookmarkUiModel(),
            )

        }
    }



    private fun saveURL()= viewModelScope.launch{
        handleLoading(true)
        val bookmark = _state.value.bookmarkData.toBookmark()
        bookmarkManagerRepo.saveBookmark(bookmark).apply {
            when(this) {
                is ProcessState.Error -> {
                    handleLoading(false)
                    _state.update { state ->
                        state.copy(
                            errorMessage = this.exception
                        )
                    }

                }

                is ProcessState.Loading -> {
                    handleLoading(true)
                    _state.update { state ->
                        state.copy(
                            errorMessage = ""
                        )
                    }
                }

                ProcessState.NotDetermined -> {

                }

                is ProcessState.Success<*> -> {
                    handleLoading(false)
                    _effects.emit(AddURLEffects.OnBack)
                }
            }

        }

    }

    private fun handleLoading(isLoading : Boolean){
        _state.update { state->
            state.copy(
                isLoading = isLoading
            )
        }
    }

    private fun handleCategorySelection(categoryItem: CategoryUiModel) {
        _state.update {
            it.copy(
                bookmarkData = it.bookmarkData.copy(
                    categoryName = categoryItem.name,
                    categoryId = categoryItem.id
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


    private fun createCategory(name : String) = viewModelScope.launch {
        bookmarkManagerRepo.createCategory(name).apply {
            when(this) {
                is ProcessState.Error -> {
                    _state.update { state ->
                        state.copy(
                            errorMessage = this.exception
                        )
                    }

                }

                is ProcessState.Loading -> {
                    _state.update { state ->
                        state.copy(
                            errorMessage = ""
                        )
                    }
                }

                ProcessState.NotDetermined -> {

                }

                is ProcessState.Success<Category> -> {
                    handleCategorySelection(this.data.toCategoryUiModel())
                }
            }
        }

    }

    private fun handleUrlChange(url : String){
        _state.update { state->
            state.copy(
                bookmarkData = state.bookmarkData.copy(
                    url = url
                )
            )
        }
    }
}

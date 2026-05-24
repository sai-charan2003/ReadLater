package com.charan.readlater.presentation.add_url

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.charan.readlater.Category
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.presentation.mapper.toBookmark
import com.charan.readlater.presentation.mapper.toBookmarkUiModel
import com.charan.readlater.presentation.mapper.toCategoryUiModel
import com.charan.readlater.presentation.mapper.toCategoryUiModelList
import com.charan.readlater.presentation.models.CategoryUiModel
import com.charan.readlater.presentation.navigation.AddURLScreenNav
import com.charan.readlater.utils.generateUuid
import com.charan.readlater.utils.getCurrentIsoDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AddURLViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val savedState : SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(AddURLState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddURLEffects>()
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
        val bookmark = bookmarkRepository.getBookmarkWithCategory(id)
        bookmark?.let {
            _state.update { state->
                state.copy(
                    bookmarkData = it.toBookmarkUiModel(),
                )
            }
        }
    }



    private fun saveURL()= viewModelScope.launch{
        if (_state.value.bookmarkData.url.isBlank()) {
            _state.update { it.copy(errorMessage = "URL cannot be empty") }
            return@launch
        }
        handleLoading(true)
        _state.update { it.copy(errorMessage = "") }
        try {
            val bookmark = _state.value.bookmarkData.toBookmark()
            bookmarkRepository.addBookmark(bookmark)
            handleLoading(false)
            _effects.emit(AddURLEffects.OnBack)
        } catch (exception: Exception) {
            handleLoading(false)
            _state.update { state ->
                state.copy(
                    errorMessage = exception.message ?: "Failed to save bookmark"
                )
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
                ),
                categorySelectBottomSheet = false
            )
        }

    }

    private fun fetchCategories() = viewModelScope.launch {
        categoryRepository.getAllActiveCategories().collectLatest { categoryEntities ->
            _state.update {
                it.copy(
                    categoryItems = categoryEntities.toCategoryUiModelList()
                )
            }
        }
    }


    private fun createCategory(name : String) = viewModelScope.launch {
        if (name.isBlank()) {
            _state.update { it.copy(errorMessage = "Category name cannot be empty") }
            return@launch
        }
        _state.update { it.copy(errorMessage = "") }
        try {
            val category = Category(
                id = generateUuid(),
                name = name,
                createdAt = getCurrentIsoDate(),
                isSynced = false,
                isDeleted = false
            )
            val savedCategory = categoryRepository.addCategory(category)
            handleCategorySelection(savedCategory.toCategoryUiModel())
            _state.update { it.copy(newCategoryName = "", categorySelectBottomSheet = false) }
        } catch (exception: Exception) {
            _state.update {
                it.copy(errorMessage = exception.message ?: "Failed to create category")
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

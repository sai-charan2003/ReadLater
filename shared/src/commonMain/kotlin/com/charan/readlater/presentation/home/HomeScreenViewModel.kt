package com.charan.readlater.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.repository.AuthenticationRepository
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.data.repository.SettingsRepository
import com.charan.readlater.data.sync.SyncManager
import com.charan.readlater.presentation.home.HomeScreenEffect.*
import com.charan.readlater.presentation.mapper.toBookmarkUiModelList
import com.charan.readlater.presentation.mapper.toCategoryItemList
import com.charan.readlater.presentation.models.BookmarkUiModel
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeScreenViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val syncManager: SyncManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeScreenEffect>()
    val effect = _effect.asSharedFlow()

    private var allBookmarks = emptyList<BookmarkUiModel>()

    init {
        drawerItemsList()
        observeBookmarks()
        observeCategories()
        viewModelScope.launch { fetchData() }
    }

    private fun observeBookmarks() = viewModelScope.launch {
        bookmarkRepository.getAllActiveBookmarksWithCategory().collectLatest { items ->
            allBookmarks = items.toBookmarkUiModelList()
            updateCategoryCounts()
            applyFilters()
        }
    }

    private fun observeCategories() = viewModelScope.launch {
        categoryRepository.getAllActiveCategories().collectLatest { categories ->
            val categoryItems = categories.toCategoryItemList(allBookmarks)
            _state.update { current ->
                val nonCategoryItems = current.navigationDrawerState.drawerItems
                    .filterNot { it is DrawerItems.Category }
                current.copy(
                    categoryItems = categoryItems,
                    navigationDrawerState = current.navigationDrawerState.copy(
                        drawerItems = nonCategoryItems + categoryItems.map { DrawerItems.Category(it) }
                    )
                )
            }
            applyFilters()
        }
    }

    fun onEvent(event : HomeScreenEvent) = viewModelScope.launch {
        when(event){

            HomeScreenEvent.OnAddURLClick -> {
                _effect.emit(NavigateToAddURLScreen(false,""))
            }

            is HomeScreenEvent.OnURLOpen -> {
                _effect.emit(OpenURLInBrowser(event.url))
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

            is HomeScreenEvent.OnDeleteBookmark -> {
                deleteBookmark(event.id)
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = false,
                    )
                }

            }

            is HomeScreenEvent.OnTabChange -> {
                _state.update { state->
                    state.copy(
                        selectedTabIndex = event.index
                    )
                }
            }

            is HomeScreenEvent.OnDueStatusChange -> {
                updateDueStatus(event.id,!event.isDue)
            }

            HomeScreenEvent.NavigateToLoginScreen -> {
                _state.update {
                    it.copy(
                        showUserNotAuthenticatedPop = false,
                    )
                }
                _effect.emit(NavigateToAuthenticationScreen)

            }

            HomeScreenEvent.OnAuthenticatedPopDismiss -> {
                _state.update {
                    it.copy(
                        showUserNotAuthenticatedPop = false
                    )
                }
                settingsRepository.updateLoginType(LoginTypeEnum.NO_ACCOUNT)

            }

            HomeScreenEvent.OnScrollToTopClick -> {
                _effect.emit(ScrollToTop)
            }

            is HomeScreenEvent.OnSearch -> {
                search(event.text)
            }

            HomeScreenEvent.OnNavigationDrawerClick -> {
                _effect.emit(ToggleNavigationDrawer)

            }
            is HomeScreenEvent.OnNavigationDrawerItemClick -> {
                _state.update {
                    it.copy(
                        navigationDrawerState = it.navigationDrawerState.copy(
                            selectedItemIndex = event.index
                        )
                    )
                }
                applyFilters()
                _effect.emit(ToggleNavigationDrawer)
            }

            is HomeScreenEvent.OnMoreItemButtonClick -> {
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = !it.showMoreOptionBottomSheet,
                        selectedBookmarkId = event.id
                    )
                }
            }
            HomeScreenEvent.OnMoreOptionBottomSheetDismiss -> {
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = false,
                        selectedBookmarkId = ""
                    )
                }
            }

            is HomeScreenEvent.OnEdit -> {
                _effect.emit(NavigateToAddURLScreen(true,event.id))
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = false,
                        selectedBookmarkId = ""
                    )
                }
            }

            HomeScreenEvent.OnDeleteCategory -> {
                val categoryUUID = _state.value.editCategoryState.categoryUUID
                if (categoryUUID.isNotBlank()) {
                    categoryRepository.deleteCategory(categoryUUID)
                }

                _state.update {
                    it.copy(
                        showDeleteCategoryDialog = false
                    )
                }


            }
            HomeScreenEvent.OnEditCategory -> {
                val currentState = _state.value.editCategoryState
                if (currentState.categoryName.isBlank()) {
                    _state.update {
                        it.copy(
                            editCategoryState = currentState.copy(
                                errorMessage = "Category name cannot be empty"
                            )
                        )
                    }
                    return@launch
                }
                categoryRepository.updateCategory(
                    categoryId = currentState.categoryUUID,
                    categoryName = currentState.categoryName
                )

                _state.update {
                    it.copy(
                        showEditCategoryDialog = false
                    )
                }

            }
            is HomeScreenEvent.OnToggleDeleteConfirmationDialog -> {
                _state.update {
                    it.copy(
                        showDeleteCategoryDialog = !it.showDeleteCategoryDialog
                    )
                }
                loadEditCategoryData(event.categoryUuid)
            }
            is HomeScreenEvent.ToggleEditCategoryDialog -> {
                _state.update {
                    it.copy(
                        showEditCategoryDialog = !it.showEditCategoryDialog
                    )
                }
                loadEditCategoryData(event.categoryUuid)
            }

            is HomeScreenEvent.OnCategoryNameChange -> {
                _state.update {
                    it.copy(
                        editCategoryState = it.editCategoryState.copy(
                            categoryName = event.name,
                            errorMessage = ""
                        )
                    )
                }
            }

            is HomeScreenEvent.OnDueButtonClick,
            is HomeScreenEvent.OnSaveURLClick,
            is HomeScreenEvent.OnURLChange -> Unit
        }
    }

    private fun loadEditCategoryData(categoryUUID : String) = viewModelScope.launch {
        if (categoryUUID.isBlank()) return@launch
        val category = categoryRepository.getCategoryById(categoryUUID)
        category?.let { data ->
            _state.update {
                it.copy(
                    editCategoryState = it.editCategoryState.copy(
                        categoryUUID = data.id,
                        categoryName = data.name,
                        errorMessage = ""
                    )
                )
            }
        }
    }

    private fun search(text : String) = viewModelScope.launch{
        if (text.isBlank()) {
            _state.update { it.copy(searchItems = emptyList()) }
            return@launch
        }
        val query = text.lowercase()
        val filtered = allBookmarks.filter { item ->
            item.title.lowercase().contains(query) ||
                item.url.lowercase().contains(query) ||
                item.description.lowercase().contains(query)
        }
        _state.update { it.copy(searchItems = filtered) }
    }

    private fun fetchData() = viewModelScope.launch {
        _state.update { it.copy(isFetchingData = true) }
        authenticationRepository.loadSession()
        syncManager.syncNow()

        val loginType = settingsRepository.getLoginType().first()
        if (loginType != LoginTypeEnum.GOOGLE) {
            _state.update { it.copy(isFetchingData = false) }
            return@launch
        }

        val categoryResult = categoryRepository.fetchCategories()
        val bookmarkResult = bookmarkRepository.fetchBookmarks()

        val errorMessage = when {
            categoryResult is ProcessState.Error -> categoryResult.exception
            bookmarkResult is ProcessState.Error -> bookmarkResult.exception
            else -> null
        }

        if (errorMessage != null) {
            _effect.emit(ShowError(errorMessage))
            _state.update { it.copy(showUserNotAuthenticatedPop = true) }
        }

        _state.update { it.copy(isFetchingData = false) }
    }

    private fun deleteBookmark(id : String) = viewModelScope.launch {
        bookmarkRepository.deleteBookmark(id)
    }

    private fun updateDueStatus(id : String , isDue : Boolean) = viewModelScope.launch {
        bookmarkRepository.updateDueStatus(id, isDue)
    }

    private fun drawerItemsList() {
        _state.update {
            it.copy(
                navigationDrawerState = it.navigationDrawerState.copy(
                    drawerItems = listOf(
                        DrawerItems.AllBookMarks,
                        DrawerItems.ReadLater
                    )
                )
            )
        }
    }

    private fun applyFilters() {
        val selectedIndex = _state.value.navigationDrawerState.selectedItemIndex
        val filtered = when (selectedIndex) {
            0 -> allBookmarks
            1 -> allBookmarks.filter { it.isDue }
            else -> {
                val category = _state.value.categoryItems.getOrNull(selectedIndex - 2)
                if (category != null) {
                    allBookmarks.filter { it.categoryId == category.uuid }
                } else {
                    allBookmarks
                }
            }
        }
        _state.update { it.copy(bookmarks = filtered) }
    }

    private fun updateCategoryCounts() {
        if (_state.value.categoryItems.isEmpty()) return
        val updatedCategories = _state.value.categoryItems.map { item ->
            item.copy(itemCount = allBookmarks.count { it.categoryId == item.uuid })
        }
        _state.update { current ->
            val nonCategoryItems = current.navigationDrawerState.drawerItems
                .filterNot { it is DrawerItems.Category }
            current.copy(
                categoryItems = updatedCategories,
                navigationDrawerState = current.navigationDrawerState.copy(
                    drawerItems = nonCategoryItems + updatedCategories.map { DrawerItems.Category(it) }
                )
            )
        }
    }

}

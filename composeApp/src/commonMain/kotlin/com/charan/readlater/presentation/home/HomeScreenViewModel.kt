package com.charan.readlater.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charan.readlater.data.local.enums.LoginTypeEnum
import com.charan.readlater.data.mappers.toCategoryUIList
import com.charan.readlater.data.mappers.toReadLaterUiItem
import com.charan.readlater.data.repository.BookmarkManagerRepo
import com.charan.readlater.data.repository.ReadLaterDataSourceRepo
import com.charan.readlater.data.repository.SettingsDataStoreRepo
import com.charan.readlater.data.repository.SupabaseRepo
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.presentation.home.HomeScreenEffect.*
import com.charan.readlater.utils.DateUtils
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val readLaterDataSourceRepo: ReadLaterDataSourceRepo,
    private val supabaseRepoImpl: SupabaseRepo,
    private val bookmarkManagerRepo: BookmarkManagerRepo,
    private val syncManager: SyncManager,
    private val settingsDataSourceRepo: SettingsDataStoreRepo
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeScreenEffect>()
    val effect = _effect.asSharedFlow()

    init {
        supabaseInit()
        getAllBookmarks()
        drawerItemsList()
        getAllCategories()
    }

    private fun supabaseInit() = viewModelScope.launch{
        loadSession()
        syncData()
        fetchData()

    }


    private suspend fun loadSession() {
        supabaseRepoImpl.loadSession()
    }
    private suspend fun syncData() {
        syncManager.sync()
    }

    private fun getAllBookmarks()= viewModelScope.launch{
        readLaterDataSourceRepo.getAllActiveItems().collectLatest { items->
            val readLaterUiItems = items.map {
                it.toReadLaterUiItem(readLaterDataSourceRepo.getCategoryByUUID(it.category_uuid ?: "")?.name ?: "")
            }
                .sortedByDescending { item -> DateUtils.isoStringToMillis(item.createdAt) }

            _state.update { state->
                state.copy(
                    readLaterUiItem = readLaterUiItems
                )
            }

        }
    }

    fun onEvent(event : HomeScreenEvent) = viewModelScope.launch {
        when(event){

            HomeScreenEvent.OnAddURLClick -> {
                _effect.emit(NavigateToAddURLScreen(false,""))
            }
            HomeScreenEvent.OnSaveURLClick -> {
//                val url = state.value.newUrlState
//                saveNewURL(url)
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

            is HomeScreenEvent.OnDueButtonClick -> {
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

            is HomeScreenEvent.OnDeleteBookmark -> {
                deleteBookmark(event.uuid)
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = false,
                        selectedBookmarkUUID = ""
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
                updateDueStatus(event.uuid,!event.isDue)
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
                settingsDataSourceRepo.updateLoginType(LoginTypeEnum.NO_ACCOUNT)

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
                filterBookmarksByIndex(event.index)
                _effect.emit(ToggleNavigationDrawer)
            }

            is HomeScreenEvent.OnMoreItemButtonClick -> {
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = !it.showMoreOptionBottomSheet,
                        selectedBookmarkUUID = event.uuid
                    )
                }
            }
            HomeScreenEvent.OnMoreOptionBottomSheetDismiss -> {
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = false,
                        selectedBookmarkUUID = ""
                    )
                }
            }

            is HomeScreenEvent.OnEdit -> {
                _effect.emit(NavigateToAddURLScreen(true,event.uuid))
                _state.update {
                    it.copy(
                        showMoreOptionBottomSheet = false,
                        selectedBookmarkUUID = ""
                    )
                }
            }
        }
    }

    private fun search(text : String) = viewModelScope.launch{
        if(text.isEmpty().not()) {
            readLaterDataSourceRepo.searchBookmarks(text).collectLatest { items ->
                _state.update {
                    it.copy(searchItems = items.toReadLaterUiItem())
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
                        if(settingsDataSourceRepo.getLoginType().first() == LoginTypeEnum.GOOGLE){
                            _state.update { it.copy(showUserNotAuthenticatedPop = true) }
                        }
                        emptyFlow()
                    }

                    is ProcessState.Loading -> {
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

                    is ProcessState.Loading -> {
                        _state.update { it.copy(isFetchingData = true) }
                    }

                    is ProcessState.Success<*> -> {
                        _state.update { it.copy(isFetchingData = false) }
                    }

                    ProcessState.NotDetermined -> {}
                }
            }
    }

    private fun deleteBookmark(uuid : String) = viewModelScope.launch {
        bookmarkManagerRepo.deleteBookmark(uuid).collectLatest {  }
    }

    private fun updateDueStatus(uuid : String , isDue : Boolean) = viewModelScope.launch {
        bookmarkManagerRepo.updateDueStatus(uuid, isDue).collectLatest { }
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

    private fun filterBookmarksByIndex(index : Int) = viewModelScope.launch {
        when(index){
            0 -> {
                readLaterDataSourceRepo.getAllActiveItems().collectLatest { items->
                    _state.update { state->
                        state.copy(
                            readLaterUiItem = items.toReadLaterUiItem()
                        )
                    }

                }
            }
            1 -> {
                readLaterDataSourceRepo.getDueItems().collectLatest { items->
                    _state.update { state->
                        state.copy(
                            readLaterUiItem = items.toReadLaterUiItem()
                        )
                    }

                }
            }
            else ->  {
                val category = _state.value.categoryItems.getOrNull(index-2)
                println(category)
                category?.let {
                    println(it.uuid)
                    readLaterDataSourceRepo.getBookmarkItemsByCategoryUUID(it.uuid).collectLatest { items->
                        _state.update { state->
                            println(items)
                            state.copy(
                                readLaterUiItem = items.toReadLaterUiItem()
                            )
                        }

                    }
                }
            }
        }
    }

    private fun getAllCategories() = viewModelScope.launch {
        readLaterDataSourceRepo.getAllCategories().collectLatest { categoryEntities ->
            _state.update {
                it.copy(
                   categoryItems = categoryEntities.toCategoryUIList(),
                    navigationDrawerState = it.navigationDrawerState.copy(
                        drawerItems = it.navigationDrawerState.drawerItems + categoryEntities.toCategoryUIList().map { DrawerItems.Category(it) }
                    )

                )
            }
        }
    }

}
package com.charan.readlater.data.sync

import com.charan.readlater.data.mappers.toBookmarkDTO
import com.charan.readlater.data.mappers.toCategoryDTO
import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.remote.model.BookmarkDTO
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.utils.ProcessState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SyncManager(
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    fun doSync() = scope.launch{
        bookmarkRepository.shouldSyncData().

    }


    fun syncData() = scope.launch {
        val email = supabaseRemoteDataSource.getUserDetails()?.email ?: return@launch
        val bookmarks = bookmarkRepository.getUnSyncedBookmarks().toBookmarkDTO(email)
        val categories = categoryRepository.getAllUnSyncedCategories().toCategoryDTO(email)
        val bookmarkJob = launch {
            supabaseRemoteDataSource.insertBookmarks(bookmarks).let {
                when(it){
                    is ProcessState.Error -> {
                        // Handle error
                    }
                    is ProcessState.Loading -> {
                        // Handle loading state if needed
                    }
                    ProcessState.NotDetermined -> {
                        // Handle not determined state if needed
                    }
                    is ProcessState.Success -> {
                        // Mark bookmarks as synced in local database
                        bookmarks.forEach { bookmark ->
                            bookmarkRepository.updateBookmarkSyncStatus(bookmark.id, true)
                        }
                    }
                }
            }

        }

        val categoryJob = launch {
            supabaseRemoteDataSource.insertCategories(categories).let {
                when(it){
                    is ProcessState.Error -> {
                        // Handle error
                    }
                    is ProcessState.Loading -> {
                        // Handle loading state if needed
                    }
                    ProcessState.NotDetermined -> {
                        // Handle not determined state if needed
                    }
                    is ProcessState.Success -> {
                        // Mark categories as synced in local database
                        categories.forEach { category ->
                            categoryRepository.updateCategorySyncStatus(category.id, true)
                        }
                    }
                }
            }
        }

        bookmarkJob.join()
        categoryJob.join()

    }



}
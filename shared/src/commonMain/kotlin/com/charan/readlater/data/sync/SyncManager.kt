package com.charan.readlater.data.sync

import com.charan.readlater.data.mappers.toBookmarkDTO
import com.charan.readlater.data.mappers.toCategoryDTO
import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository
import com.charan.readlater.utils.ProcessState

expect class SyncManager {
    suspend fun syncNow()
    suspend fun scheduleSync()
    suspend fun syncListener()
    internal suspend fun performSync(): SyncOutcome
}

internal enum class SyncOutcome {
    Success,
    Skipped,
    Retry
}

internal class SyncCoordinator(
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource
) {
    suspend fun performSync(): SyncOutcome {
        supabaseRemoteDataSource.loadSession()
        val email = supabaseRemoteDataSource.getUserDetails()?.email?.takeIf { it.isNotBlank() }
            ?: return SyncOutcome.Skipped

        var didWork = false
        var shouldRetry = false

        val unSyncedCategories = categoryRepository.getAllUnSyncedCategories()
        if (unSyncedCategories.isNotEmpty()) {
            didWork = true
            val categoryDTOs = unSyncedCategories.toCategoryDTO(email)
            when (supabaseRemoteDataSource.insertCategories(categoryDTOs)) {
                is ProcessState.Success -> {
                    categoryDTOs.forEach { category ->
                        categoryRepository.updateCategorySyncStatus(category.id, true)
                    }
                }

                ProcessState.NotDetermined,
                is ProcessState.Error,
                is ProcessState.Loading -> shouldRetry = true
            }
        }

        val unSyncedBookmarks = bookmarkRepository.getUnSyncedBookmarks()
        if (unSyncedBookmarks.isNotEmpty()) {
            didWork = true
            val bookmarkDTOs = unSyncedBookmarks.toBookmarkDTO(email)
            when (supabaseRemoteDataSource.insertBookmarks(bookmarkDTOs)) {
                is ProcessState.Success -> {
                    bookmarkDTOs.forEach { bookmark ->
                        bookmarkRepository.updateBookmarkSyncStatus(bookmark.id, true)
                    }
                }

                ProcessState.NotDetermined,
                is ProcessState.Error,
                is ProcessState.Loading -> shouldRetry = true
            }
        }

        return when {
            shouldRetry -> SyncOutcome.Retry
            didWork -> SyncOutcome.Success
            else -> SyncOutcome.Skipped
        }
    }
}

package com.charan.readlater.data.sync

import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository

actual class SyncManager(
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource
) {
    private val syncCoordinator = SyncCoordinator(
        bookmarkRepository = bookmarkRepository,
        categoryRepository = categoryRepository,
        supabaseRemoteDataSource = supabaseRemoteDataSource
    )

    actual suspend fun syncNow() {
        syncCoordinator.performSync()
    }

    actual suspend fun scheduleSync() = Unit

    actual suspend fun syncListener() {
        syncNow()
    }

    internal actual suspend fun performSync(): SyncOutcome {
        return syncCoordinator.performSync()
    }
}

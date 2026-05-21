package com.charan.readlater.data.sync

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.charan.readlater.data.remote.SupabaseRemoteDataSource
import com.charan.readlater.data.repository.BookmarkRepository
import com.charan.readlater.data.repository.CategoryRepository

actual class SyncManager(
    context: Context,
    private val bookmarkRepository: BookmarkRepository,
    private val categoryRepository: CategoryRepository,
    private val supabaseRemoteDataSource: SupabaseRemoteDataSource
) {
    private val appContext = context.applicationContext
    private val syncCoordinator = SyncCoordinator(
        bookmarkRepository = bookmarkRepository,
        categoryRepository = categoryRepository,
        supabaseRemoteDataSource = supabaseRemoteDataSource
    )

    actual suspend fun syncNow() {
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            SyncWork.SYNC_NOW_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            SyncWork.oneTimeRequest()
        )
    }

    actual suspend fun scheduleSync() {
        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            SyncWork.SYNC_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            SyncWork.periodicRequest()
        )
    }

    actual suspend fun syncListener() {
        scheduleSync()
        syncNow()
    }

    internal actual suspend fun performSync(): SyncOutcome {
        return syncCoordinator.performSync()
    }
}

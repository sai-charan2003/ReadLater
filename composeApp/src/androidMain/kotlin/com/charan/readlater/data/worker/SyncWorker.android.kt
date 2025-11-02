package com.charan.readlater.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.charan.readlater.data.repository.SyncManager
import com.charan.readlater.data.repository.impl.SyncManagerImpl

actual class SyncWorker(
    private val context : Context,
    private val workerParams : WorkerParameters,
    private val syncRepo : SyncManager
) : CoroutineWorker(context,workerParams) {

    override suspend fun doWork(): Result {
        syncRepo.sync()
        return Result.success()
    }
}

actual fun doSyncWork() {
    val workRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
    WorkManager.getInstance().enqueueUniqueWork(
        "SyncWork",
        ExistingWorkPolicy.KEEP,
        workRequest
    )
}
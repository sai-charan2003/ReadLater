package com.charan.readlater.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import java.time.Duration

class SyncWork(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, workerParams) {


    companion object {
        const val SYNC_NOW_WORK_NAME = "SyncWorkNow"
        const val SYNC_PERIODIC_WORK_NAME = "SyncWorkPeriodic"
        fun periodicRequest(): PeriodicWorkRequest {
            val constraints =
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()

            return PeriodicWorkRequestBuilder<SyncWork>(repeatInterval = Duration.ofMinutes(15))
                .setConstraints(constraints)
                .build()
        }


        fun oneTimeRequest(): OneTimeWorkRequest {
            val constraints =
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()

            return OneTimeWorkRequestBuilder<SyncWork>()
                .setConstraints(constraints)
                .build()
        }
    }
    override suspend fun doWork(): Result {
        return when (syncManager.performSync()) {
            SyncOutcome.Retry -> Result.retry()
            SyncOutcome.Success,
            SyncOutcome.Skipped -> Result.success()
        }
    }
}

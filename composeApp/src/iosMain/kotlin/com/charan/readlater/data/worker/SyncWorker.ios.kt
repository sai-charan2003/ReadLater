package com.charan.readlater.data.worker

import com.charan.readlater.data.repository.SyncManager
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import platform.BackgroundTasks.BGAppRefreshTask
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSDate
import platform.Foundation.addTimeInterval
import platform.Foundation.dateByAddingTimeInterval

actual class SyncWorker(
    private val syncManager: SyncManager
) {

    companion object {


    }


}

fun registerTask(
    syncManager: SyncManager
) {
    println("Registering background task")
    BGTaskScheduler.sharedScheduler.registerForTaskWithIdentifier(
        identifier = "com.charan.readlater.apprefresh",
        usingQueue = null
    ) { task ->
        CoroutineScope(Dispatchers.IO).launch {
            syncManager.sync()
        }
        println("From background task")

        scheduleAppRefreshTask()
        (task as? BGAppRefreshTask)?.setTaskCompletedWithSuccess(true)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun scheduleAppRefreshTask() {
    println("Scheduling background task")
    val request = BGAppRefreshTaskRequest(identifier = "com.charan.readlater.apprefresh")
    request.earliestBeginDate = NSDate().dateByAddingTimeInterval((15 * 60).toDouble())
    BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
}

actual fun doSyncWork() {
    scheduleAppRefreshTask()

}
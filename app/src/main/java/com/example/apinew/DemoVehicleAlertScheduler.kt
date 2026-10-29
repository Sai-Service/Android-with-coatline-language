package com.example.apinew

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Kicks off the background check for demo vehicles out >3hrs. Only runs while
 * the logged-in session's role is "Team Leader". The worker re-schedules itself
 * after every run (see DemoVehicleAlertWorker.scheduleNextCheck), so this only
 * needs to fire the first link in the chain.
 */
object DemoVehicleAlertScheduler {

    private const val TAG = "DemoVehicleAlert"

    fun scheduleIfTeamLeader(context: Context, session: UserSession) {
        if (!session.role.equals("Team Leader", ignoreCase = true)) {
            Log.d(TAG, "scheduleIfTeamLeader: role '${session.role}' is not Team Leader — not scheduling")
            return
        }

        // REPLACE clears any prior run stuck in retry/backoff from an earlier session,
        // and runs immediately instead of waiting out a delay from the last chain link.
        val request = OneTimeWorkRequestBuilder<DemoVehicleAlertWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            DemoVehicleAlertWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d(TAG, "scheduleIfTeamLeader: kicked off overdue-check chain")
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DemoVehicleAlertWorker.WORK_NAME)
    }
}

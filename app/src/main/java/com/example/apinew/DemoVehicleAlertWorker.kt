package com.example.apinew

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Periodically checks demoVehStatusList for vehicles that have been "OUT FOR DEMO"
 * for 3+ hours and notifies the logged-in Team Leader, re-notifying every
 * [CHECK_INTERVAL_MINUTES] while a vehicle stays overdue. Self-reschedules after
 * each run (see [scheduleNextCheck]) — kicked off by DemoVehicleAlertScheduler.
 * Runs only while a Team Leader session is active.
 */
class DemoVehicleAlertWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "demo_vehicle_overdue_check"
        private const val TAG = "DemoVehicleAlert"
        private const val CHANNEL_ID = "demo_vehicle_overdue"
        private const val OVERDUE_HOURS = 3

        // TESTING: re-check / re-notify every 1 minute. Change to 60 for production.
        private const val CHECK_INTERVAL_MINUTES = 1L

        private const val NOTIFIED_PREFS = "demo_vehicle_notified_prefs"
        private const val NOTIFIED_KEY = "notified_times"      // JSON: dedupeKey -> last-notified epoch millis
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val session = SessionManager.getSession(applicationContext)
        if (session == null) {
            Log.d(TAG, "doWork: no session saved — skipping")
            return@withContext Result.success()   // logged out — nothing to do
        }

        Log.d(TAG, "doWork: session role='${session.role}' ouId=${session.ouId} " +
            "location='${session.locationName}' loginName='${session.loginName}'")

        if (!session.role.equals("Team Leader", ignoreCase = true)) {
            Log.d(TAG, "doWork: role '${session.role}' is not Team Leader — skipping")
            return@withContext Result.success()       // no longer a Team Leader session
        }

        try {
            val url = URL(
                "${ApiFile.ERP_APP_URL}/demoVehicleTrans/demoVehStatusList" +
                    "?ouId=${session.ouId}&location=${session.locationName}&loginName=${session.loginName}"
            )
            Log.d(TAG, "doWork: calling $url")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "doWork: response = $responseText")
            val dataArray = JSONObject(responseText).optJSONArray("obj")
            if (dataArray == null) {
                Log.d(TAG, "doWork: no 'obj' array in response — skipping")
                return@withContext Result.success()
            }
            Log.d(TAG, "doWork: ${dataArray.length()} row(s) returned")

            val prefs = applicationContext.getSharedPreferences(NOTIFIED_PREFS, Context.MODE_PRIVATE)
            val notifiedTimes = JSONObject(prefs.getString(NOTIFIED_KEY, null) ?: "{}")
            val now = System.currentTimeMillis()
            val reminderIntervalMs = TimeUnit.MINUTES.toMillis(CHECK_INTERVAL_MINUTES)
            var changed = false
            val stillOutKeys = mutableSetOf<String>()

            for (i in 0 until dataArray.length()) {
                val obj = dataArray.getJSONObject(i)
                val status = obj.optString("STATUS")
                val vehicleNoForLog = obj.optString("INSTANCE_NUMBER")
                if (!status.contains("OUT", ignoreCase = true)) {
                    Log.d(TAG, "doWork: $vehicleNoForLog status='$status' — not OUT, skipping")
                    continue
                }

                val outTimeRaw = obj.optString("OUT_TIME")
                val hoursOut = hoursSince(outTimeRaw)
                if (hoursOut == null) {
                    Log.d(TAG, "doWork: $vehicleNoForLog outTimeRaw='$outTimeRaw' failed to parse — skipping")
                    continue
                }
                Log.d(TAG, "doWork: $vehicleNoForLog outTimeRaw='$outTimeRaw' hoursOut=$hoursOut")
                if (hoursOut < OVERDUE_HOURS) {
                    Log.d(TAG, "doWork: $vehicleNoForLog only $hoursOut h out, under $OVERDUE_HOURS h threshold — skipping")
                    continue
                }

                val vehicleNo = obj.optString("INSTANCE_NUMBER")
                val executiveName = obj.optString("ATTRIBUTE3")
                    .takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
                    ?: obj.optString("CREATED_BY")

                val dedupeKey = "$vehicleNo|$outTimeRaw"
                stillOutKeys.add(dedupeKey)
                val lastNotified = if (notifiedTimes.has(dedupeKey)) notifiedTimes.getLong(dedupeKey) else null
                if (lastNotified != null && now - lastNotified < reminderIntervalMs) {
                    Log.d(TAG, "doWork: $vehicleNo reminded ${(now - lastNotified) / 1000}s ago, " +
                        "under ${reminderIntervalMs / 1000}s interval — skipping")
                    continue
                }

                Log.d(TAG, "doWork: notifying for $vehicleNo (out $hoursOut h, exec=$executiveName)")
                showNotification(vehicleNo, executiveName)
                notifiedTimes.put(dedupeKey, now)
                changed = true
            }

            // Drop entries for vehicles that are no longer out, so returned-then-redemoed
            // vehicles (new OUT_TIME) and old test data don't linger forever.
            val staleKeys = notifiedTimes.keys().asSequence().filterNot { it in stillOutKeys }.toList()
            if (staleKeys.isNotEmpty()) {
                staleKeys.forEach { notifiedTimes.remove(it) }
                changed = true
            }

            if (changed) {
                prefs.edit().putString(NOTIFIED_KEY, notifiedTimes.toString()).apply()
            }

            scheduleNextCheck()
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: exception — ${e.javaClass.simpleName}: ${e.message}", e)
            Result.retry()
        }
    }

    /** Re-enqueues itself after [CHECK_INTERVAL_MINUTES] so the check keeps repeating. */
    private fun scheduleNextCheck() {
        val request = OneTimeWorkRequestBuilder<DemoVehicleAlertWorker>()
            .setInitialDelay(CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_NAME, ExistingWorkPolicy.REPLACE, request
        )
    }

    /** OUT_TIME comes back as e.g. "2025-06-18T10:17:03.000+0000" (UTC). */
    private fun hoursSince(rawIso: String): Double? {
        if (rawIso.isBlank()) return null
        return try {
            val cleaned = rawIso.substringBefore(".").take(19)
            val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val outMillis = fmt.parse(cleaned)?.time ?: return null
            (System.currentTimeMillis() - outMillis) / (1000.0 * 60 * 60)
        } catch (e: Exception) {
            null
        }
    }

    private fun showNotification(vehicleNo: String, executiveName: String) {
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Demo Vehicle Alerts", NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(
                applicationContext, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return   // not granted — can't post
        }

        val openIntent = Intent(applicationContext, WorkshopAvailableDemoVehicle::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            vehicleNo.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.apkicon)
            .setContentTitle("Demo Vehicle Still Out")
            .setContentText("Vehicle $vehicleNo out by $executiveName is still out")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(vehicleNo.hashCode(), notification)
    }
}

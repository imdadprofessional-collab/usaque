package com.cdlpermitprep.usa.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cdlpermitprep.usa.R
import com.cdlpermitprep.usa.data.preferences.UserPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/** Daily study reminder, study-goal nudge, and exam countdown — all driven from the same worker. */
@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userPreferences: UserPreferences,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "daily_reminder_work"
        private const val CHANNEL_ID = "study_reminders"
    }

    override suspend fun doWork(): Result {
        val enabled = userPreferences.notificationsEnabled.first()
        if (!enabled) return Result.success()

        val examDate = userPreferences.examDateEpoch.first()
        val message = examDate?.let {
            val daysLeft = ((it - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).coerceAtLeast(0)
            "Your CDL exam is in $daysLeft day(s). Keep practicing!"
        } ?: "Time for today's CDL practice — keep your streak alive!"

        showNotification(message)
        return Result.success()
    }

    private fun showNotification(message: String) {
        val manager = applicationContext.getSystemService(NotificationManager::class.java) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Study Reminders", NotificationManager.IMPORTANCE_DEFAULT),
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("CDL Permit Prep")
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        manager.notify(1001, notification)
    }
}

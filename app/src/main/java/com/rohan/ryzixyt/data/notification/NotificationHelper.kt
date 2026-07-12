package com.rohan.ryzixyt.data.notification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.rohan.ryzixyt.RyzixApplication

/** Thin wrapper so download progress/completion notifications stay consistent app-wide. */
object NotificationHelper {

    fun showProgress(context: Context, notificationId: Int, title: String, percent: Int) {
        if (!hasPermission(context)) return
        val notification = NotificationCompat.Builder(context, RyzixApplication.DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText("Downloading — $percent%")
            .setProgress(100, percent, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        managerOf(context).notify(notificationId, notification)
    }

    fun showDone(context: Context, notificationId: Int, title: String) {
        if (!hasPermission(context)) return
        val notification = NotificationCompat.Builder(context, RyzixApplication.DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText("Download complete")
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
        managerOf(context).notify(notificationId, notification)
    }

    fun showFailed(context: Context, notificationId: Int, title: String, reason: String) {
        if (!hasPermission(context)) return
        val notification = NotificationCompat.Builder(context, RyzixApplication.DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(title)
            .setContentText("Download failed — $reason")
            .setAutoCancel(true)
            .build()
        managerOf(context).notify(notificationId, notification)
    }

    private fun managerOf(context: Context) =
        context.getSystemService(NotificationManager::class.java)

    private fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

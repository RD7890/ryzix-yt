package com.rohan.ryzixyt

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.rohan.ryzixyt.data.remote.OkHttpDownloader
import dagger.hilt.android.HiltAndroidApp
import org.schabi.newpipe.extractor.NewPipe

@HiltAndroidApp
class RyzixApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // NewPipeExtractor needs exactly one Downloader registered for the process lifetime.
        NewPipe.init(OkHttpDownloader.instance)
        createDownloadNotificationChannel()
    }

    private fun createDownloadNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            DOWNLOAD_CHANNEL_ID,
            getString(R.string.download_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.download_channel_desc)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val DOWNLOAD_CHANNEL_ID = "ryzix_downloads"
    }
}

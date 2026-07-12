package com.rohan.ryzixyt.data.download

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohan.ryzixyt.data.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

/**
 * Downloads one stream URL to public storage while reporting progress through the
 * system notification (see [NotificationHelper]) — there is no separate in-app feed.
 */
class DownloadWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val client = OkHttpClient.Builder().build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val streamUrl = inputData.getString(KEY_STREAM_URL) ?: return@withContext Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: "Ryzix YT video"
        val quality = inputData.getString(KEY_QUALITY_LABEL) ?: ""
        val extension = inputData.getString(KEY_EXTENSION) ?: "mp4"
        val notificationId = id.toString().hashCode()

        return@withContext try {
            val moviesDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                ?: applicationContext.filesDir
            val outDir = File(moviesDir, "RyzixYT").apply { mkdirs() }
            val safeTitle = title.take(80).replace(Regex("[\\\\/:*?\"<>|]"), "_")
            val outFile = File(outDir, "$safeTitle-$quality.$extension")

            val request = Request.Builder().url(streamUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IllegalStateException("HTTP ${response.code}")
                val body = response.body ?: throw IllegalStateException("Empty response body")
                val total = body.contentLength()
                var written = 0L
                var lastPercent = -1

                body.byteStream().use { input ->
                    FileOutputStream(outFile).use { output ->
                        val buffer = ByteArray(64 * 1024)
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            written += read
                            if (total > 0) {
                                val percent = ((written * 100) / total).toInt()
                                if (percent != lastPercent) {
                                    lastPercent = percent
                                    setProgressAsync(androidx.work.Data.Builder().putInt(KEY_PROGRESS, percent).build())
                                    NotificationHelper.showProgress(applicationContext, notificationId, title, percent)
                                }
                            }
                        }
                    }
                }
            }

            NotificationHelper.showDone(applicationContext, notificationId, title)
            Result.success()
        } catch (e: Exception) {
            NotificationHelper.showFailed(applicationContext, notificationId, title, e.message ?: "Unknown error")
            Result.failure()
        }
    }

    companion object {
        const val KEY_STREAM_URL = "stream_url"
        const val KEY_TITLE = "title"
        const val KEY_QUALITY_LABEL = "quality_label"
        const val KEY_EXTENSION = "extension"
        const val KEY_PROGRESS = "progress"
    }
}

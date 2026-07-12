package com.rohan.ryzixyt.ui.player

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rohan.ryzixyt.data.download.DownloadWorker
import com.rohan.ryzixyt.data.local.WatchHistoryDao
import com.rohan.ryzixyt.data.local.WatchHistoryEntity
import com.rohan.ryzixyt.data.model.StreamOption
import com.rohan.ryzixyt.data.model.VideoDetails
import com.rohan.ryzixyt.data.repository.YoutubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val details: VideoDetails? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val downloadQueuedLabel: String? = null,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    private val repository: YoutubeRepository,
    private val historyDao: WatchHistoryDao,
) : AndroidViewModel(application) {

    var uiState by mutableStateOf(PlayerUiState())
        private set

    fun load(videoUrl: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            runCatching { repository.fetchDetails(videoUrl) }
                .onSuccess { details ->
                    uiState = uiState.copy(details = details, isLoading = false)
                    historyDao.upsert(
                        WatchHistoryEntity(
                            videoId = details.videoId,
                            url = details.url,
                            title = details.title,
                            uploaderName = details.uploaderName,
                            thumbnailUrl = details.thumbnailUrl,
                            durationSeconds = details.durationSeconds,
                            watchedAtEpochMs = System.currentTimeMillis(),
                        ),
                    )
                }
                .onFailure { e -> uiState = uiState.copy(isLoading = false, error = e.message ?: "Could not load video") }
        }
    }

    fun startDownload(option: StreamOption) {
        val title = uiState.details?.title ?: "Ryzix YT video"
        val extension = if (option.isAudioOnly) "m4a" else "mp4"
        val data = Data.Builder()
            .putString(DownloadWorker.KEY_STREAM_URL, option.streamUrl)
            .putString(DownloadWorker.KEY_TITLE, title)
            .putString(DownloadWorker.KEY_QUALITY_LABEL, option.label)
            .putString(DownloadWorker.KEY_EXTENSION, extension)
            .build()
        val request = OneTimeWorkRequestBuilder<DownloadWorker>().setInputData(data).build()
        WorkManager.getInstance(getApplication()).enqueue(request)
        uiState = uiState.copy(downloadQueuedLabel = option.label)
    }

    fun clearDownloadNotice() {
        uiState = uiState.copy(downloadQueuedLabel = null)
    }
}

package com.rohan.ryzixyt.ui.notifications

import androidx.lifecycle.ViewModel
import com.rohan.ryzixyt.data.download.DownloadEventsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    repository: DownloadEventsRepository,
) : ViewModel() {
    val events: StateFlow<List<com.rohan.ryzixyt.data.download.DownloadEvent>> = repository.events
}

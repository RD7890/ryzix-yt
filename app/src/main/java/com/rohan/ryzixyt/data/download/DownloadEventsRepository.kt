package com.rohan.ryzixyt.data.download

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-process feed backing the Notifications screen. WorkManager updates it as
 * downloads progress; the UI observes it as a StateFlow for real-time updates.
 */
@Singleton
class DownloadEventsRepository @Inject constructor() {

    private val _events = MutableStateFlow<List<DownloadEvent>>(emptyList())
    val events: StateFlow<List<DownloadEvent>> = _events

    fun upsert(event: DownloadEvent) {
        _events.update { current ->
            val withoutExisting = current.filterNot { it.id == event.id }
            (listOf(event) + withoutExisting).sortedByDescending { it.createdAtEpochMs }
        }
    }

    companion object {
        // Single process-wide instance reachable from the non-Hilt WorkManager worker.
        @Volatile private var held: DownloadEventsRepository? = null

        fun bind(instance: DownloadEventsRepository) {
            held = instance
        }

        fun getOrNull(): DownloadEventsRepository? = held
    }
}

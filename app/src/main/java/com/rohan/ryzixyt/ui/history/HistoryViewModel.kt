package com.rohan.ryzixyt.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.ryzixyt.data.local.WatchHistoryDao
import com.rohan.ryzixyt.data.local.WatchHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val dao: WatchHistoryDao,
) : ViewModel() {

    val history: StateFlow<List<WatchHistoryEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun remove(videoId: String) {
        viewModelScope.launch { dao.delete(videoId) }
    }

    fun clearAll() {
        viewModelScope.launch { dao.clear() }
    }
}

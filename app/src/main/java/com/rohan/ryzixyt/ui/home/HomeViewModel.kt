package com.rohan.ryzixyt.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.ryzixyt.data.model.VideoResult
import com.rohan.ryzixyt.data.repository.YoutubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val query: String = "",
    val results: List<VideoResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: YoutubeRepository,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            uiState = uiState.copy(results = emptyList(), isLoading = false, error = null)
            return
        }
        searchJob = viewModelScope.launch {
            delay(350) // debounce so real-time typing doesn't spam the extractor
            runSearch(query)
        }
    }

    fun retry() = runSearch(uiState.query)

    private fun runSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            runCatching { repository.search(query) }
                .onSuccess { results -> uiState = uiState.copy(results = results, isLoading = false) }
                .onFailure { e -> uiState = uiState.copy(isLoading = false, error = e.message ?: "Search failed") }
        }
    }
}

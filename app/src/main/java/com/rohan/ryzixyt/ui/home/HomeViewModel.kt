package com.rohan.ryzixyt.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohan.ryzixyt.data.model.HOME_CATEGORIES
import com.rohan.ryzixyt.data.model.VideoResult
import com.rohan.ryzixyt.data.repository.YoutubeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val query: String = "",
    val selectedCategory: String = HOME_CATEGORIES.first().id,
    val results: List<VideoResult> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSearching: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: YoutubeRepository,
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    private var loadJob: Job? = null

    init {
        loadCategory(uiState.selectedCategory)
    }

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        loadJob?.cancel()
        if (query.isBlank()) {
            loadCategory(uiState.selectedCategory)
            return
        }
        loadJob = viewModelScope.launch {
            delay(350) // debounce so real-time typing doesn't spam the extractor
            runSearch(query)
        }
    }

    fun onCategorySelected(categoryId: String) {
        if (uiState.query.isNotBlank()) uiState = uiState.copy(query = "")
        loadCategory(categoryId)
    }

    fun retry() {
        if (uiState.query.isBlank()) loadCategory(uiState.selectedCategory) else runSearch(uiState.query)
    }

    private fun loadCategory(categoryId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, selectedCategory = categoryId, isSearching = false)
            runCatching { repository.forCategory(categoryId) }
                .onSuccess { results -> uiState = uiState.copy(results = results, isLoading = false) }
                .onFailure { e -> uiState = uiState.copy(isLoading = false, error = e.message ?: "Could not load videos") }
        }
    }

    private fun runSearch(query: String) {
        if (query.isBlank()) return
        loadJob = viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null, isSearching = true)
            runCatching { repository.search(query) }
                .onSuccess { results -> uiState = uiState.copy(results = results, isLoading = false) }
                .onFailure { e -> uiState = uiState.copy(isLoading = false, error = e.message ?: "Search failed") }
        }
    }
}

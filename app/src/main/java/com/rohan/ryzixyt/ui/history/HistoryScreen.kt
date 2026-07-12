package com.rohan.ryzixyt.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rohan.ryzixyt.data.model.VideoResult
import com.rohan.ryzixyt.ui.components.RyzixTopBar
import com.rohan.ryzixyt.ui.components.VideoCard

@Composable
fun HistoryScreen(
    onVideoClick: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsState()

    Scaffold(topBar = { RyzixTopBar(title = "Watch History") }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (history.isEmpty()) {
                Text(
                    text = "Videos you open will show up here",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(history, key = { it.videoId }) { entry ->
                        VideoCard(
                            result = VideoResult(
                                videoId = entry.videoId,
                                url = entry.url,
                                title = entry.title,
                                uploaderName = entry.uploaderName,
                                thumbnailUrl = entry.thumbnailUrl,
                                durationSeconds = entry.durationSeconds,
                            ),
                            onClick = { onVideoClick(entry.url) },
                        )
                    }
                }
            }
        }
    }
}

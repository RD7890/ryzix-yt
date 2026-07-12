package com.rohan.ryzixyt.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rohan.ryzixyt.data.local.WatchHistoryEntity
import com.rohan.ryzixyt.ui.components.RyzixTopBar
import com.rohan.ryzixyt.util.formatRelativeTime

@Composable
fun HistoryScreen(
    onVideoClick: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            RyzixTopBar(
                title = "Watch History",
                actions = {
                    if (history.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearAll) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteSweep,
                                contentDescription = "Clear all",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (history.isEmpty()) {
                EmptyHistoryState()
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(history, key = { it.videoId }) { entry ->
                        HistoryRow(
                            entry = entry,
                            onClick = { onVideoClick(entry.url) },
                            onRemove = { viewModel.remove(entry.videoId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(56.dp),
        )
        Text(
            text = "No watch history yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = "Videos you open will show up here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun HistoryRow(entry: WatchHistoryEntity, onClick: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(130.dp)
                .height(74.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AsyncImage(
                model = entry.thumbnailUrl,
                contentDescription = entry.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            if (entry.durationSeconds > 0) {
                Text(
                    text = formatDuration(entry.durationSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${entry.uploaderName} • Watched ${formatRelativeTime(entry.watchedAtEpochMs)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Remove from history",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds) else "%d:%02d".format(minutes, seconds)
}

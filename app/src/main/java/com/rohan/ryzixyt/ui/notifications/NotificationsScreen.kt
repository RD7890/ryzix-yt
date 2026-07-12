package com.rohan.ryzixyt.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Downloading
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rohan.ryzixyt.data.download.DownloadEvent
import com.rohan.ryzixyt.data.download.DownloadState
import com.rohan.ryzixyt.ui.components.RyzixTopBar

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = hiltViewModel()) {
    val events by viewModel.events.collectAsState()

    Scaffold(topBar = { RyzixTopBar(title = "Notifications") }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (events.isEmpty()) {
                Text(
                    text = "Download activity will show up here",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(events, key = { it.id }) { event -> NotificationRow(event) }
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(event: DownloadEvent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val (icon, tint) = when (event.state) {
                DownloadState.QUEUED -> Icons.Outlined.HourglassEmpty to MaterialTheme.colorScheme.onSurfaceVariant
                DownloadState.RUNNING -> Icons.Outlined.Downloading to MaterialTheme.colorScheme.primary
                DownloadState.DONE -> Icons.Outlined.CheckCircle to MaterialTheme.colorScheme.primary
                DownloadState.FAILED -> Icons.Outlined.ErrorOutline to MaterialTheme.colorScheme.error
            }
            Icon(imageVector = icon, contentDescription = null, tint = tint)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${event.qualityLabel} — ${event.state.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (event.state == DownloadState.RUNNING) {
            LinearProgressIndicator(
                progress = { event.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

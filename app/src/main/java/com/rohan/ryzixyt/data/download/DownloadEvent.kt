package com.rohan.ryzixyt.data.download

enum class DownloadState { QUEUED, RUNNING, DONE, FAILED }

data class DownloadEvent(
    val id: String,
    val title: String,
    val qualityLabel: String,
    val state: DownloadState,
    val progressPercent: Int = 0,
    val filePath: String? = null,
    val createdAtEpochMs: Long,
)

package com.rohan.ryzixyt.util

import java.util.concurrent.TimeUnit

/** Small, dependency-free relative-time formatter for history rows. */
fun formatRelativeTime(epochMs: Long, nowMs: Long = System.currentTimeMillis()): String {
    val diff = (nowMs - epochMs).coerceAtLeast(0)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours h ago"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        days < 30 -> "${days / 7} wk ago"
        days < 365 -> "${days / 30} mo ago"
        else -> "${days / 365} yr ago"
    }
}

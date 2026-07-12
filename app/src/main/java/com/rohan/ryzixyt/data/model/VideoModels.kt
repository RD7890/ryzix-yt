package com.rohan.ryzixyt.data.model

/** A single search result row, shown on the Home screen. */
data class VideoResult(
    val videoId: String,
    val url: String,
    val title: String,
    val uploaderName: String,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    val viewCountLabel: String? = null,
    val uploadedLabel: String? = null,
)

/** One selectable stream a user can play or download. */
data class StreamOption(
    val formatId: String,
    val label: String,
    val approxSizeLabel: String?,
    val isAudioOnly: Boolean,
    val isPremiumTier: Boolean,
    val streamUrl: String,
    val mimeType: String,
)

/** Full detail payload for the player screen. */
data class VideoDetails(
    val videoId: String,
    val url: String,
    val title: String,
    val uploaderName: String,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    val viewCountLabel: String? = null,
    val hlsUrl: String?,
    val dashUrl: String?,
    val playbackUrl: String?,
    val videoOptions: List<StreamOption>,
    val audioOptions: List<StreamOption>,
)

/** A quick-access content shelf on the Home screen, Vidmate-style. */
data class HomeCategory(val id: String, val label: String)

val HOME_CATEGORIES = listOf(
    HomeCategory("trending", "Trending"),
    HomeCategory("music", "Music"),
    HomeCategory("gaming", "Gaming"),
    HomeCategory("movies", "Movies"),
    HomeCategory("news", "News"),
    HomeCategory("sports", "Sports"),
)

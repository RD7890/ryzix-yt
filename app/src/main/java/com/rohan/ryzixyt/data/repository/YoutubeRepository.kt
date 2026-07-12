package com.rohan.ryzixyt.data.repository

import com.rohan.ryzixyt.data.model.StreamOption
import com.rohan.ryzixyt.data.model.VideoDetails
import com.rohan.ryzixyt.data.model.VideoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

/**
 * Thin domain layer on top of NewPipeExtractor. All blocking extraction calls are
 * pushed onto Dispatchers.IO so the UI stays responsive.
 */
@Singleton
class YoutubeRepository @Inject constructor() {

    private val youtubeService = ServiceList.YouTube

    suspend fun search(query: String): List<VideoResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val extractor = youtubeService.getSearchExtractor(query)
        extractor.fetchPage()
        extractor.initialPage.items
            .filterIsInstance<org.schabi.newpipe.extractor.stream.StreamInfoItem>()
            .map { item ->
                VideoResult(
                    videoId = extractVideoId(item.url),
                    url = item.url,
                    title = item.name,
                    uploaderName = item.uploaderName ?: "Unknown",
                    thumbnailUrl = item.thumbnails.maxByOrNull { it.height }?.url,
                    durationSeconds = item.duration,
                )
            }
    }

    suspend fun fetchDetails(videoUrl: String): VideoDetails = withContext(Dispatchers.IO) {
        val info = StreamInfo.getInfo(youtubeService, videoUrl)

        val videoOptions = info.videoStreams
            .filter { !it.isVideoOnly }
            .sortedByDescending { resolutionHeight(it.resolution) }
            .mapIndexed { index, stream ->
                StreamOption(
                    formatId = stream.itagItem?.id?.toString() ?: "v$index",
                    label = stream.resolution ?: "Auto",
                    approxSizeLabel = null,
                    isAudioOnly = false,
                    isPremiumTier = index == 0,
                    streamUrl = stream.content,
                    mimeType = stream.format?.mimeType ?: "video/mp4",
                )
            }

        val audioOptions = info.audioStreams
            .sortedByDescending { it.averageBitrate }
            .mapIndexed { index, stream ->
                StreamOption(
                    formatId = stream.itagItem?.id?.toString() ?: "a$index",
                    label = "${stream.averageBitrate} kbps",
                    approxSizeLabel = null,
                    isAudioOnly = true,
                    isPremiumTier = index == 0,
                    streamUrl = stream.content,
                    mimeType = stream.format?.mimeType ?: "audio/mp4",
                )
            }

        VideoDetails(
            videoId = extractVideoId(videoUrl),
            url = videoUrl,
            title = info.name,
            uploaderName = info.uploaderName ?: "Unknown",
            thumbnailUrl = info.thumbnails.maxByOrNull { it.height }?.url,
            durationSeconds = info.duration,
            hlsUrl = info.hlsUrl,
            dashUrl = info.dashMpdUrl,
            playbackUrl = videoOptions.firstOrNull()?.streamUrl,
            videoOptions = videoOptions,
            audioOptions = audioOptions,
        )
    }

    private fun resolutionHeight(resolution: String?): Int =
        resolution?.filter { it.isDigit() }?.takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0

    private fun extractVideoId(url: String): String =
        runCatching { NewPipe.getService(0) }.let {
            // v= query param is the stable id for standard watch URLs.
            val match = Regex("[?&]v=([^&]+)").find(url)
            match?.groupValues?.get(1) ?: url.substringAfterLast('/').substringBefore('?')
        }
}

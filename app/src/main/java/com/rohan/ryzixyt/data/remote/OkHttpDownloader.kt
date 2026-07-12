package com.rohan.ryzixyt.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request as NPRequest
import org.schabi.newpipe.extractor.downloader.Response as NPResponse
import java.util.concurrent.TimeUnit

/**
 * NewPipeExtractor requires exactly one [Downloader] implementation registered via
 * NewPipe.init(). We back it with OkHttp so the whole app shares one HTTP client/pool.
 */
class OkHttpDownloader private constructor() : Downloader() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun execute(request: NPRequest): NPResponse {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()

        val builder = Request.Builder().url(url)
        for ((key, values) in headers) {
            for (value in values) builder.addHeader(key, value)
        }

        val body = request.dataToSend()
        when (httpMethod) {
            "POST" -> builder.post((body ?: ByteArray(0)).toRequestBody())
            "PUT" -> builder.put((body ?: ByteArray(0)).toRequestBody())
            else -> builder.get()
        }
        if (headers["User-Agent"].isNullOrEmpty()) {
            builder.addHeader("User-Agent", USER_AGENT)
        }

        client.newCall(builder.build()).execute().use { response ->
            val bodyString = response.body?.string() ?: ""
            return NPResponse(
                response.code,
                response.message,
                response.headers.toMultimap(),
                bodyString,
                response.request.url.toString(),
            )
        }
    }

    companion object {
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14) RyzixYT/1.0"
        val instance: OkHttpDownloader by lazy { OkHttpDownloader() }
    }
}

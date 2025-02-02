package com.reactnativecustomtimernotification

import android.graphics.Bitmap
import android.graphics.Movie
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.io.IOException
import kotlin.math.min

private suspend fun fetchGifFromUrl(gifUrl: String): InputStream = withContext(Dispatchers.IO) {
    Log.d("GifProcessor", "Fetching GIF from URL: $gifUrl")

    val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val request = Request.Builder().url(gifUrl).build()

    val response = client.newCall(request).execute()
    if (!response.isSuccessful) {
        throw IOException("Failed to download GIF: ${response.message}")
    }

    response.body?.byteStream() ?: throw IOException("No response body available")
}

private suspend fun gifToFrames(gifInputStream: InputStream, memoryLimitMB: Int = 2): List<Bitmap> = withContext(Dispatchers.Default) {
    Log.d("GifProcessor", "Processing GIF to extract frames")

    val frames = mutableListOf<Bitmap>()
    val movie = Movie.decodeStream(gifInputStream) ?: throw IllegalArgumentException("Failed to decode GIF")

    val width = movie.width()
    val height = movie.height()
    val duration = movie.duration()

    if (duration <= 0) throw IllegalArgumentException("Invalid GIF duration")

    val maxFrames = 50
    val frameInterval = (duration / maxFrames).coerceAtLeast(100)
    val maxDimension = 1024

    val scaledWidth = width.coerceAtMost(maxDimension)
    val scaledHeight = height.coerceAtMost(maxDimension)

    val bytesPerFrame = scaledWidth * scaledHeight * 4L
    val memoryLimitBytes = memoryLimitMB * 1024L * 1024L
    val maxFramesForMemory = min((memoryLimitBytes / bytesPerFrame).toInt(), maxFrames)

    Log.d("GifProcessor", "Memory limit allows for $maxFramesForMemory frames")

    val bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    var currentMemoryUsage = 0L

    try {
        var time = 0
        while (time < duration && frames.size < maxFramesForMemory) {
            yield()

            if (currentMemoryUsage + bytesPerFrame > memoryLimitBytes) {
                Log.d("GifProcessor", "Memory limit reached, stopping frame extraction")
                break
            }

            movie.setTime(time)
            canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
            movie.draw(canvas, 0f, 0f)

            val frameBitmap = Bitmap.createBitmap(bitmap)
            frames.add(frameBitmap)

            // Update current memory usage for each frame added
            currentMemoryUsage += bytesPerFrame
            time += frameInterval
        }
    } finally {
        gifInputStream.close()
        bitmap.recycle()
    }

    Log.d("GifProcessor", "Extracted ${frames.size} frames from GIF, using approximately ${currentMemoryUsage / 1024 / 1024}MB")
    frames
}

// Main entry point to process GIF from URL
suspend fun processGif(gifUrl: String, memoryLimitMB: Int = 2): List<Bitmap> = withContext(Dispatchers.IO) {
    try {
        fetchGifFromUrl(gifUrl).use { gifInputStream ->
            gifToFrames(gifInputStream, memoryLimitMB)
        }
    } catch (e: Exception) {
        Log.e("GifProcessor", "Error processing GIF: ${e.message}", e)
        throw e
    }
}

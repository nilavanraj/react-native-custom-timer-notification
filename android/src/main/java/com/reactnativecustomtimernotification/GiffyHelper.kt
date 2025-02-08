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

class GifProcessor {
    companion object {
        private const val TAG = "GifProcessor"
        private const val MAX_FRAMES = 50
        private const val MIN_FRAME_INTERVAL = 100
        private const val MAX_DIMENSION = 1024
        private const val BYTES_PER_PIXEL = 4L
    }

    suspend fun fetchGifFromUrl(gifUrl: String): InputStream = withContext(Dispatchers.IO) {
        Log.d(TAG, "Starting GIF fetch from URL: $gifUrl")

        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        try {
            Log.d(TAG, "Creating request...")
            val request = Request.Builder()
                .url(gifUrl)
                .build()

            Log.d(TAG, "Executing request...")
            val response = client.newCall(request).execute()

            Log.d(TAG, "Response received. Success: ${response.isSuccessful}, Code: ${response.code}")
            Log.d(TAG, "Response headers: ${response.headers}")

            if (!response.isSuccessful) {
                throw IOException("HTTP error! Status: ${response.code}, Message: ${response.message}")
            }

            val contentType = response.header("Content-Type")
            Log.d(TAG, "Content-Type: $contentType")

            val contentLength = response.header("Content-Length")
            Log.d(TAG, "Content-Length: $contentLength")

            val body = response.body
            if (body == null) {
                Log.e(TAG, "Response body is null!")
                throw IOException("No response body available")
            }

            Log.d(TAG, "Successfully retrieved response body")
            return@withContext body.byteStream()

        } catch (e: Exception) {
            Log.e(TAG, "Error during GIF fetch: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private suspend fun gifToFrames(
        gifInputStream: InputStream,
        memoryLimitMB: Int = 2
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        Log.d(TAG, "Processing GIF to extract frames")

        val movie = Movie.decodeStream(gifInputStream)
            ?: throw IllegalArgumentException("Failed to decode GIF")

        val scaledDimensions = calculateScaledDimensions(movie.width(), movie.height())
        val bytesPerFrame = scaledDimensions.first * scaledDimensions.second * BYTES_PER_PIXEL
        val maxFramesForMemory = calculateMaxFrames(bytesPerFrame, memoryLimitMB)

        Log.d(TAG, "Memory limit allows for $maxFramesForMemory frames")

        val frames = mutableListOf<Bitmap>()
        var currentMemoryUsage = 0L

        try {
            val bitmap = Bitmap.createBitmap(
                scaledDimensions.first,
                scaledDimensions.second,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)

            extractFrames(
                movie,
                canvas,
                bitmap,
                frames,
                maxFramesForMemory,
                bytesPerFrame,
                memoryLimitMB,
                currentMemoryUsage
            )
        } finally {
            gifInputStream.close()
        }

        Log.d(TAG, "Extracted ${frames.size} frames from GIF")
        frames
    }

    private fun calculateScaledDimensions(width: Int, height: Int): Pair<Int, Int> {
        val scaledWidth = width.coerceAtMost(MAX_DIMENSION)
        val scaledHeight = height.coerceAtMost(MAX_DIMENSION)
        return Pair(scaledWidth, scaledHeight)
    }

    private fun calculateMaxFrames(bytesPerFrame: Long, memoryLimitMB: Int): Int {
        val memoryLimitBytes = memoryLimitMB * 1024L * 1024L
        return min((memoryLimitBytes / bytesPerFrame).toInt(), MAX_FRAMES)
    }

    private suspend fun extractFrames(
        movie: Movie,
        canvas: android.graphics.Canvas,
        bitmap: Bitmap,
        frames: MutableList<Bitmap>,
        maxFramesForMemory: Int,
        bytesPerFrame: Long,
        memoryLimitMB: Int,
        currentMemoryUsage: Long
    ) {
        val duration = movie.duration().takeIf { it > 0 }
            ?: throw IllegalArgumentException("Invalid GIF duration")

        val frameInterval = (duration / MAX_FRAMES).coerceAtLeast(MIN_FRAME_INTERVAL)
        val memoryLimitBytes = memoryLimitMB * 1024L * 1024L
        var time = 0
        var updatedMemoryUsage = currentMemoryUsage

        try {
            while (time < duration && frames.size < maxFramesForMemory) {
                yield()

                if (updatedMemoryUsage + bytesPerFrame > memoryLimitBytes) {
                    Log.d(TAG, "Memory limit reached, stopping frame extraction")
                    break
                }

                movie.setTime(time)
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
                movie.draw(canvas, 0f, 0f)

                val frameBitmap = Bitmap.createBitmap(bitmap)
                frames.add(frameBitmap)

                updatedMemoryUsage += bytesPerFrame
                time += frameInterval
            }
        } finally {
            bitmap.recycle()
        }
    }

    suspend fun processGif(gifUrl: String, memoryLimitMB: Int = 2): List<Bitmap> =
        withContext(Dispatchers.IO) {
            try {
                fetchGifFromUrl(gifUrl).use { gifInputStream ->
                    gifToFrames(gifInputStream, memoryLimitMB)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing GIF: ${e.message}", e)
                throw e
            }
        }
}
package com.audiotranslator.audio

import android.content.Context
import android.media.MediaPlayer
import java.io.File
import java.io.FileOutputStream

actual class AudioPlayer(private val context: Context) {

    actual var callback: AudioPlayerCallback? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playing = false

    actual fun play(audioBytes: ByteArray) {
        stop()
        try {
            val tempFile = File.createTempFile("playback_", ".mp3", context.cacheDir)
            FileOutputStream(tempFile).use { it.write(audioBytes) }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                setOnPreparedListener {
                    playing = true
                    callback?.onPlaybackStarted()
                    start()
                }
                setOnCompletionListener {
                    playing = false
                    tempFile.delete()
                    release()
                    mediaPlayer = null
                    callback?.onPlaybackCompleted()
                }
                setOnErrorListener { _, what, extra ->
                    playing = false
                    tempFile.delete()
                    callback?.onError("Playback error: what=$what extra=$extra")
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            callback?.onError(e.message ?: "Playback error")
        }
    }

    actual fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
            playing = false
        }
    }

    actual fun isPlaying(): Boolean = playing

    actual fun release() {
        stop()
    }
}

package com.audiotranslator.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

actual class AudioRecorder(private val context: Context) {

    actual var callback: AudioRecorderCallback? = null
    private var mediaRecorder: MediaRecorder? = null
    private var tempFile: File? = null
    private var recording = false

    actual fun startRecording() {
        try {
            tempFile = File.createTempFile("recording_", ".m4a", context.cacheDir)

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(tempFile!!.absolutePath)
                prepare()
                start()
            }
            recording = true
            callback?.onRecordingStarted()
        } catch (e: Exception) {
            recording = false
            mediaRecorder?.release()
            mediaRecorder = null
            callback?.onError(e.message ?: "Failed to start recording")
        }
    }

    actual fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaRecorder = null
            recording = false
        }
        tempFile?.delete()
        tempFile = null
        // do NOT invoke callback
    }

    actual fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // stop() can throw if recording hadn't started properly
        } finally {
            mediaRecorder = null
            recording = false
        }
        val bytes = runCatching { tempFile?.readBytes() }.getOrDefault(ByteArray(0)) ?: ByteArray(0)
        tempFile?.delete()
        tempFile = null
        callback?.onRecordingStopped(bytes)
    }

    actual fun isRecording(): Boolean = recording
}

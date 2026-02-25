package com.audiotranslator.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.timeIntervalSince1970
import platform.posix.memcpy

actual class AudioRecorder {

    actual var callback: AudioRecorderCallback? = null
    private var audioRecorder: AVAudioRecorder? = null
    private var outputUrl: NSURL? = null
    private var recording = false

    @OptIn(ExperimentalForeignApi::class)
    actual fun startRecording() {
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryRecord, error = null)
            session.setActive(true, error = null)

            val tempDir = platform.Foundation.NSTemporaryDirectory()
            val fileName = "recording_${NSDate().timeIntervalSince1970}.m4a"
            outputUrl = NSURL.fileURLWithPath("$tempDir$fileName")

            val settings = mapOf<Any?, Any?>(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to 44100.0,
                AVNumberOfChannelsKey to 1,
                AVEncoderAudioQualityKey to AVAudioQualityHigh
            )

            var error: NSError? = null
            val recorder = AVAudioRecorder(outputUrl!!, settings, error = null)
            if (recorder == null) {
                callback?.onError("Failed to create audio recorder")
                return
            }
            audioRecorder = recorder
            audioRecorder?.record()
            recording = true
            callback?.onRecordingStarted()
        } catch (e: Exception) {
            recording = false
            callback?.onError(e.message ?: "Failed to start recording")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun cancelRecording() {
        audioRecorder?.stop()
        audioRecorder = null
        recording = false
        outputUrl?.let { url ->
            NSFileManager.defaultManager.removeItemAtURL(url, error = null)
        }
        outputUrl = null
        // do NOT invoke callback
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun stopRecording() {
        audioRecorder?.stop()
        recording = false
        audioRecorder = null

        val url = outputUrl
        outputUrl = null

        if (url != null) {
            val data = NSData.dataWithContentsOfURL(url)
            val bytes = data?.toByteArray() ?: ByteArray(0)
            NSFileManager.defaultManager.removeItemAtURL(url, error = null)
            callback?.onRecordingStopped(bytes)
        } else {
            callback?.onRecordingStopped(ByteArray(0))
        }
    }

    actual fun isRecording(): Boolean = recording

    @OptIn(ExperimentalForeignApi::class)
    private fun NSData.toByteArray(): ByteArray {
        val arr = ByteArray(length.toInt())
        if (length > 0u) {
            arr.usePinned { pinned ->
                memcpy(pinned.addressOf(0), bytes, length)
            }
        }
        return arr
    }
}

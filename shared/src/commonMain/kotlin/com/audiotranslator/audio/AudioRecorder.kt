package com.audiotranslator.audio

interface AudioRecorderCallback {
    fun onRecordingStarted()
    fun onRecordingStopped(audioBytes: ByteArray)
    fun onError(message: String)
}

expect class AudioRecorder {
    fun startRecording()
    fun stopRecording()
    fun cancelRecording()
    fun isRecording(): Boolean
    var callback: AudioRecorderCallback?
}

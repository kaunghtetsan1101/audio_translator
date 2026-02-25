package com.audiotranslator.audio

interface AudioPlayerCallback {
    fun onPlaybackStarted()
    fun onPlaybackCompleted()
    fun onError(message: String)
}

expect class AudioPlayer {
    fun play(audioBytes: ByteArray)
    fun stop()
    fun isPlaying(): Boolean
    var callback: AudioPlayerCallback?
    fun release()
}

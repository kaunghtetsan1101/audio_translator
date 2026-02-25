package com.audiotranslator.audio

expect class AudioFilePicker {
    fun pickAudioFile(onFilePicked: (ByteArray?) -> Unit)
}

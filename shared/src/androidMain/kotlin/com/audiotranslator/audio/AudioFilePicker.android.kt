package com.audiotranslator.audio

import android.content.Context
import android.net.Uri

actual class AudioFilePicker(private val context: Context) {

    private var pendingCallback: ((ByteArray?) -> Unit)? = null
    private var launcher: (() -> Unit)? = null

    fun setLauncher(launch: () -> Unit) {
        launcher = launch
    }

    fun onActivityResult(uri: Uri?) {
        if (uri == null) {
            pendingCallback?.invoke(null)
            pendingCallback = null
            return
        }
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            pendingCallback?.invoke(bytes)
        } catch (e: Exception) {
            pendingCallback?.invoke(null)
        } finally {
            pendingCallback = null
        }
    }

    actual fun pickAudioFile(onFilePicked: (ByteArray?) -> Unit) {
        pendingCallback = onFilePicked
        launcher?.invoke()
    }
}

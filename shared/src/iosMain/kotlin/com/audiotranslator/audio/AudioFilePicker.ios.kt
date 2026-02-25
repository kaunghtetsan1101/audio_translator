package com.audiotranslator.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject
import platform.posix.memcpy

actual class AudioFilePicker {

    @OptIn(ExperimentalForeignApi::class)
    actual fun pickAudioFile(onFilePicked: (ByteArray?) -> Unit) {
        val types = listOf("public.audio", "com.apple.m4a-audio", "public.mp3")
        val picker = UIDocumentPickerViewController(
            documentTypes = types,
            inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
        )

        val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(
                controller: UIDocumentPickerViewController,
                didPickDocumentsAtURLs: List<*>
            ) {
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                val bytes = url?.let {
                    NSData.dataWithContentsOfURL(it)?.toByteArray()
                }
                onFilePicked(bytes)
            }

            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                onFilePicked(null)
            }
        }

        picker.delegate = delegate

        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(picker, animated = true, completion = null)
    }

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

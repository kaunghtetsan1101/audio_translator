package com.audiotranslator.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.dataWithBytes
import platform.darwin.NSObject
import platform.posix.memcpy

actual class AudioPlayer {

    actual var callback: AudioPlayerCallback? = null
    private var audioPlayer: AVAudioPlayer? = null
    private var playerDelegate: AVAudioPlayerDelegateProtocol? = null
    private var playing = false

    @OptIn(ExperimentalForeignApi::class)
    actual fun play(audioBytes: ByteArray) {
        stop()
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, error = null)
            session.setActive(true, error = null)

            val data = audioBytes.usePinned { pinned ->
                NSData.dataWithBytes(pinned.addressOf(0), audioBytes.size.toULong())
            }

            val delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
                override fun audioPlayerDidFinishPlaying(
                    player: AVAudioPlayer,
                    successfully: Boolean
                ) {
                    playing = false
                    callback?.onPlaybackCompleted()
                }

                override fun audioPlayerDecodeErrorDidOccur(
                    player: AVAudioPlayer,
                    error: NSError?
                ) {
                    playing = false
                    callback?.onError(error?.localizedDescription ?: "Decode error")
                }
            }
            playerDelegate = delegate

            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                val player = AVAudioPlayer(data = data, error = errorPtr.ptr)
                if (player == null) {
                    callback?.onError(errorPtr.value?.localizedDescription ?: "Failed to create player")
                    return
                }
                player.delegate = delegate
                player.prepareToPlay()
                player.play()
                audioPlayer = player
                playing = true
                callback?.onPlaybackStarted()
            }
        } catch (e: Exception) {
            callback?.onError(e.message ?: "Playback error")
        }
    }

    actual fun stop() {
        audioPlayer?.stop()
        audioPlayer = null
        playerDelegate = null
        playing = false
    }

    actual fun isPlaying(): Boolean = playing

    actual fun release() {
        stop()
    }
}

package com.audiotranslator

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.audiotranslator.audio.AudioFilePicker
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val audioFilePicker: AudioFilePicker by inject()

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        audioFilePicker.onActivityResult(uri)
    }

    private val recordAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* permission result handled by the OS; recording will work on next attempt */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        audioFilePicker.setLauncher { filePickerLauncher.launch("audio/*") }
        recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        setContent {
            App()
        }
    }
}

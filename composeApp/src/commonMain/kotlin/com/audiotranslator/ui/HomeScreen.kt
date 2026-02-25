package com.audiotranslator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.audiotranslator.domain.model.TranslationMode
import com.audiotranslator.presentation.TranslatorViewModel
import com.audiotranslator.ui.components.AudioInputSection
import com.audiotranslator.ui.components.LanguageSelectorSection
import com.audiotranslator.ui.components.ResultSection
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TranslatorViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Audio Translator") })
        }
    ) { padding ->
        if (state.isLoadingLanguages) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TranslationMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = state.mode == mode,
                        onClick = { viewModel.onModeChanged(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, TranslationMode.entries.size),
                        label = { Text(if (mode == TranslationMode.TRANSCRIBE) "Transcribe" else "Translate") }
                    )
                }
            }

            AudioInputSection(
                isRecording = state.isRecording,
                hasAudio = state.hasAudioInput,
                pickedFileName = state.pickedFileName,
                onToggleRecord = viewModel::onToggleRecording,
                onPickFile = viewModel::onPickAudioFile,
                onCancelRecord = viewModel::onCancelRecording,
                onClearRecording = viewModel::onClearRecording
            )

            if (state.mode == TranslationMode.TRANSLATE) {
                LanguageSelectorSection(
                    languages = state.languages,
                    selectedLanguage = state.selectedLanguage,
                    voices = state.voices,
                    selectedVoice = state.selectedVoice,
                    isLoadingVoices = state.isLoadingVoices,
                    onLanguageSelected = viewModel::onLanguageSelected,
                    onVoiceSelected = viewModel::onVoiceSelected
                )
            }

            val actionLabel = if (state.mode == TranslationMode.TRANSCRIBE) "Transcribe" else "Translate"
            Button(
                onClick = viewModel::onTranslate,
                enabled = state.canTranslate,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("${actionLabel}ing...")
                } else {
                    Text(actionLabel)
                }
            }

            state.translationResult?.let { result ->
                ResultSection(
                    result = result,
                    hasAudio = state.translatedAudioBytes != null,
                    isPlaying = state.isPlayingAudio,
                    onPlayAudio = viewModel::onPlayTranslatedAudio,
                    onClearResult = viewModel::onClearResult
                )
            }

            state.errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::onDismissError) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

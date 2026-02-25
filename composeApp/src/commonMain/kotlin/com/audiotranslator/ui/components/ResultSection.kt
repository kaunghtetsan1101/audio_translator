package com.audiotranslator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.audiotranslator.domain.model.TranslationResult

@Composable
fun ResultSection(
    result: TranslationResult,
    hasAudio: Boolean,
    isPlaying: Boolean,
    onPlayAudio: () -> Unit,
    onClearResult: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Results",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onClearResult) {
                    Text("Clear")
                }
            }

            result.detectedLanguage?.let { detected ->
                Text(
                    text = "Detected language: $detected",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            HorizontalDivider()

            Text("Transcription", style = MaterialTheme.typography.labelMedium)
            Text(result.transcription, style = MaterialTheme.typography.bodyMedium)

            val translation = result.translation
            if (translation != null) {
                HorizontalDivider()

                Text(
                    text = "Translation (${result.targetLanguage})",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(translation, style = MaterialTheme.typography.bodyMedium)
            }

            if (hasAudio) {
                HorizontalDivider()
                AudioPlaybackControls(
                    isPlaying = isPlaying,
                    onTogglePlay = onPlayAudio
                )
            }
        }
    }
}

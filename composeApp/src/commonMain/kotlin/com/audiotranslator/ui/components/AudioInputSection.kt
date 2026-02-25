package com.audiotranslator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioInputSection(
    isRecording: Boolean,
    hasAudio: Boolean,
    pickedFileName: String?,
    onToggleRecord: () -> Unit,
    onPickFile: () -> Unit,
    onCancelRecord: () -> Unit,
    onClearRecording: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Audio Input", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onToggleRecord,
                    colors = if (isRecording) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isRecording) "Stop" else "Record")
                }

                if (isRecording) {
                    OutlinedButton(
                        onClick = onCancelRecord,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                } else {
                    OutlinedButton(
                        onClick = onPickFile,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pick File")
                    }
                }
            }

            if (hasAudio && !isRecording) {
                TextButton(onClick = onClearRecording) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            }

            when {
                isRecording -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Recording...", style = MaterialTheme.typography.bodySmall)
                    }
                }
                pickedFileName != null -> {
                    Text(
                        text = "File loaded: $pickedFileName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                hasAudio -> {
                    Text(
                        text = "Recording ready",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

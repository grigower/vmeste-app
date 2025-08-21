package com.example.tzapp.ui.trainers

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

enum class SoundCategory { BIRDS, MUSIC, HOME }

@Composable
fun SoundRecognitionExercise(level: Level, coOp: Boolean, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val tone = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    var idx by remember { mutableIntStateOf(0) }
    var correct by remember { mutableStateOf(false) }

    val sequence = when (level) {
        Level.Easy -> listOf(SoundCategory.BIRDS, SoundCategory.MUSIC, SoundCategory.HOME)
        Level.Medium -> listOf(SoundCategory.MUSIC, SoundCategory.BIRDS, SoundCategory.HOME)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Узнавание звуков", style = MaterialTheme.typography.titleLarge)
        Button(onClick = {
            // simulate different categories with different tones
            when (sequence[idx % sequence.size]) {
                SoundCategory.BIRDS -> tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
                SoundCategory.MUSIC -> tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
                SoundCategory.HOME -> tone.startTone(ToneGenerator.TONE_SUP_RINGTONE, 400)
            }
        }) { Text("▶ Воспроизвести") }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElevatedCard(onClick = {
                correct = sequence[idx % sequence.size] == SoundCategory.BIRDS
                if (correct) idx++
                if (idx >= sequence.size) onSuccess()
            }, modifier = Modifier.weight(1f)) { Text("Птицы", modifier = Modifier.padding(12.dp)) }
            ElevatedCard(onClick = {
                correct = sequence[idx % sequence.size] == SoundCategory.MUSIC
                if (correct) idx++
                if (idx >= sequence.size) onSuccess()
            }, modifier = Modifier.weight(1f)) { Text("Музыка", modifier = Modifier.padding(12.dp)) }
            ElevatedCard(onClick = {
                correct = sequence[idx % sequence.size] == SoundCategory.HOME
                if (correct) idx++
                if (idx >= sequence.size) onSuccess()
            }, modifier = Modifier.weight(1f)) { Text("Бытовые", modifier = Modifier.padding(12.dp)) }
        }
        if (coOp) Text("Подсказка: опишите звук, если сложно узнать")
    }
}


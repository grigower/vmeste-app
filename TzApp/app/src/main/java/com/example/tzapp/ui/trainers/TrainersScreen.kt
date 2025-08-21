package com.example.tzapp.ui.trainers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrainersScreen(onGoHome: (() -> Unit)? = null) {
    var coOp by remember { mutableStateOf(false) }
    var level by remember { mutableStateOf(Level.Easy) }
    var current by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Тренажёры для памяти", style = MaterialTheme.typography.titleLarge)
            if (onGoHome != null) {
                TextButton(onClick = onGoHome) { Text("На главную") }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = level == Level.Easy, onClick = { level = Level.Easy }, label = { Text("Лёгкий") })
            FilterChip(selected = level == Level.Medium, onClick = { level = Level.Medium }, label = { Text("Средний") })
            FilterChip(selected = coOp, onClick = { coOp = !coOp }, label = { Text("Совместно") })
        }

        if (current == null) {
            Text("Выберите упражнение")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { current = "assoc" }) { Text("Ассоциации") }
                Button(onClick = { current = "pics" }) { Text("Картинки") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { current = "odd" }) { Text("Что лишнее?") }
                OutlinedButton(onClick = { current = "sound" }) { Text("Звуки") }
            }
        } else when (current) {
            "assoc" -> AssociationsExercise(level = level, coOp = coOp) { current = null }
            "pics" -> PictureMemoryExercise(level = level, coOp = coOp) { current = null }
            "odd" -> OddOneOutExercise(level = level, coOp = coOp) { current = null }
            "sound" -> SoundRecognitionExercise(level = level, coOp = coOp) { current = null }
        }
    }
}


package com.example.tzapp.ui.trainers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Association(val left: String, val right: String, val wrong: String)

private fun sampleAssociations(level: Level): List<Association> = when (level) {
    Level.Easy -> listOf(
        Association("кофе", "чашка", "ложка"),
        Association("книга", "страница", "вилка"),
        Association("ключ", "замок", "окно"),
    )
    Level.Medium -> listOf(
        Association("почта", "конверт", "лампа"),
        Association("машина", "дорога", "дверь"),
        Association("музыка", "ноты", "сковорода"),
    )
}

enum class Level { Easy, Medium }

@Composable
fun AssociationsExercise(level: Level, coOp: Boolean, onSuccess: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var correctShown by remember { mutableStateOf(false) }
    val list = remember(level) { sampleAssociations(level) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ассоциации", style = MaterialTheme.typography.titleLarge)
        if (coOp) {
            Text("Подсказка для ухаживающего: проговорите связь между словами.")
        }
        if (index >= list.size) {
            SuccessAnimated(onContinue = onSuccess)
            return@Column
        }
        val task = list[index]
        Text("Что подходит к слову \"${task.left}\"?")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ElevatedCard(onClick = { correctShown = true }, modifier = Modifier.weight(1f)) { Text(task.right, modifier = Modifier.padding(16.dp)) }
            ElevatedCard(onClick = { correctShown = false }, modifier = Modifier.weight(1f)) { Text(task.wrong, modifier = Modifier.padding(16.dp)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { correctShown = true }) { Text("Подсказка") }
            Button(onClick = { if (correctShown) index++ }) { Text("Дальше") }
        }
        AnimatedVisibility(visible = correctShown, enter = fadeIn() + scaleIn(), exit = fadeOut() + scaleOut()) {
            Text("Молодец!", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun SuccessAnimated(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Молодец!", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = onContinue) { Text("Продолжить") }
    }
}


package com.example.tzapp.ui.trainers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class OddSet(val options: List<String>, val oddIndex: Int)

private fun oddSets(level: Level): List<OddSet> = when (level) {
    Level.Easy -> listOf(
        OddSet(listOf("яблоко", "груша", "банан", "молоток"), 3),
        OddSet(listOf("стол", "стул", "шкаф", "собака"), 3)
    )
    Level.Medium -> listOf(
        OddSet(listOf("вода", "сок", "молоко", "ножницы"), 3),
        OddSet(listOf("ночь", "утро", "вечер", "гвоздь"), 3)
    )
}

@Composable
fun OddOneOutExercise(level: Level, coOp: Boolean, onSuccess: () -> Unit) {
    val tasks = remember(level) { oddSets(level) }
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Что лишнее?", style = MaterialTheme.typography.titleLarge)
        if (index >= tasks.size) {
            Text("Молодец!", style = MaterialTheme.typography.headlineMedium)
            onSuccess()
            return@Column
        }
        val t = tasks[index]
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            t.options.forEachIndexed { i, text ->
                ElevatedCard(onClick = {
                    correct = i == t.oddIndex
                    if (correct) index++
                }, modifier = Modifier.weight(1f)) { Text(text, modifier = Modifier.padding(12.dp)) }
            }
        }
        if (coOp) Text("Подсказка: обсудите, что объединяет остальные слова")
    }
}


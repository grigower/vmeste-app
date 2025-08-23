package com.example.tzapp.ui.trainers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tzapp.R

data class PictureCard(val id: Int, val iconRes: Int)

private fun iconSet(level: Level): List<PictureCard> = when (level) {
    Level.Easy -> listOf(R.drawable.ic_home, R.drawable.ic_person, R.drawable.ic_planner, R.drawable.ic_info)
    Level.Medium -> listOf(R.drawable.ic_search, R.drawable.ic_settings, R.drawable.ic_diary, R.drawable.ic_recommendations)
}.mapIndexed { idx, res -> PictureCard(idx, res) }

@Composable
fun PictureMemoryExercise(level: Level, coOp: Boolean, onSuccess: () -> Unit) {
    var revealed by remember { mutableStateOf(setOf<Int>()) }
    val cards = remember(level) { iconSet(level) + iconSet(level) } // pairs

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Память на картинки", style = MaterialTheme.typography.titleLarge)
        if (coOp) Text("Подсказка: помогайте словами, а не касанием экрана")
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cards) { card ->
                val isShown = revealed.contains(card.id)
                ElevatedCard(onClick = {
                    revealed = if (isShown) revealed - card.id else revealed + card.id
                    if (revealed.size >= cards.distinctBy { it.id }.size) onSuccess()
                }, modifier = Modifier.padding(6.dp)) {
                    AnimatedVisibility(visible = isShown, enter = fadeIn(), exit = fadeOut()) {
                        Icon(painter = painterResource(id = card.iconRes), contentDescription = null, modifier = Modifier.padding(24.dp))
                    }
                }
            }
        }
    }
}


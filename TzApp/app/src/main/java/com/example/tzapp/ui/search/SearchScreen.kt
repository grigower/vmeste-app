package com.example.tzapp.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tzapp.data.Item
import com.example.tzapp.data.Repository

@Composable
fun SearchScreen(onOpen: (Int) -> Unit) {
	var query by remember { mutableStateOf("") }
	val items: List<Item> = remember(query) {
		Repository.items.filter { item ->
			item.title.contains(query, ignoreCase = true) ||
			item.description.contains(query, ignoreCase = true)
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		OutlinedTextField(
			value = query,
			onValueChange = { query = it },
			label = { Text("Поиск") },
			modifier = Modifier
				.fillMaxWidth()
		)
		LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
			items(items) { item ->
				ElevatedCard(
					onClick = { onOpen(item.id) },
					modifier = Modifier.padding(vertical = 6.dp)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Text(item.title)
						Text(item.description, modifier = Modifier.padding(top = 4.dp))
					}
				}
			}
		}
	}
}


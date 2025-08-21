package com.example.tzapp.ui.reco

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import com.example.tzapp.R

enum class RecoCategory(val title: String) {
    HOME("Уход и безопасность дома"),
    FOOD("Питание и режим дня"),
    COMM("Общение с человеком с деменцией"),
    PSY("Психологическая поддержка"),
    LAW("Юридические и социальные вопросы");

    companion object {
        fun fromString(value: String): RecoCategory = entries.firstOrNull { it.name == value } ?: HOME
    }
}

data class RecommendationArticle(
    val id: Int,
    val title: String,
    val author: String,
    val category: RecoCategory,
    val content: String,
)

private fun loadArticles(context: Context): List<RecommendationArticle> {
    val json = context.assets.open("articles.json").bufferedReader().use { it.readText() }
    val arr = JSONArray(json)
    val result = mutableListOf<RecommendationArticle>()
    for (i in 0 until arr.length()) {
        val o: JSONObject = arr.getJSONObject(i)
        result.add(
            RecommendationArticle(
                id = o.getInt("id"),
                title = o.getString("title"),
                author = o.optString("author", ""),
                category = RecoCategory.fromString(o.getString("category")),
                content = o.getString("content"),
            )
        )
    }
    return result
}

@Composable
fun RecommendationsScreen(onOpen: (Int) -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<RecoCategory?>(null) }
    val favorites = remember { mutableStateListOf<Int>() }
    var articles by remember { mutableStateOf<List<RecommendationArticle>>(emptyList()) }

    LaunchedEffect(Unit) { articles = loadArticles(context) }

    val filtered = remember(query, selectedCategory, articles, favorites) {
        articles.filter { a ->
            (selectedCategory == null || a.category == selectedCategory) &&
            (query.isBlank() || a.title.contains(query, true) || a.content.contains(query, true))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Поиск по ключевым словам") },
            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)
        )
        androidx.compose.foundation.layout.Row(modifier = Modifier.padding(bottom = 8.dp)) {
            RecoCategory.entries.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                    label = { Text(cat.title) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            items(filtered) { article ->
                ElevatedCard(onClick = { onOpen(article.id) }, modifier = Modifier.padding(vertical = 6.dp)) {
                    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
                        Text(article.title, style = MaterialTheme.typography.titleMedium)
                        Text(article.author, style = MaterialTheme.typography.bodySmall)
                        androidx.compose.foundation.layout.Row(modifier = Modifier.padding(top = 8.dp)) {
                            Text(article.category.title, modifier = Modifier.weight(1f))
                            IconButton(onClick = {
                                if (favorites.contains(article.id)) favorites.remove(article.id) else favorites.add(article.id)
                            }) {
                                val icon = if (favorites.contains(article.id)) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                                Icon(painter = painterResource(id = icon), contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationDetailScreen(id: Int) {
    val context = LocalContext.current
    var article by remember { mutableStateOf<RecommendationArticle?>(null) }
    LaunchedEffect(id) {
        article = loadArticles(context).firstOrNull { it.id == id }
    }
    val a = article
    if (a != null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(a.title, style = MaterialTheme.typography.headlineSmall)
            Text(a.author, style = MaterialTheme.typTypography.bodySmall)
            Text(a.content, modifier = Modifier.padding(top = 12.dp))
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Статья не найдена")
        }
    }
}


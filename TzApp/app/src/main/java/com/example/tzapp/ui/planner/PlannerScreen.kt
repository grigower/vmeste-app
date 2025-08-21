package com.example.tzapp.ui.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.flow.map
import com.example.tzapp.data.planner.PlannerRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tzapp.data.planner.PlannerEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class EventType(val label: String, val color: Color) {
    MEDS("Лекарства", Color(0xFF2962FF)),
    DOCTOR("Врачи", Color(0xFFFF1744)),
    EVENT("Мероприятия", Color(0xFF2E7D32))
}

private fun PlannerEventEntity.toUi(): PlannerEventUi = PlannerEventUi(
    id = id,
    title = title,
    type = when (type) {
        "MEDS" -> EventType.MEDS
        "DOCTOR" -> EventType.DOCTOR
        else -> EventType.EVENT
    },
    dateTime = dateTimeText,
    reminderMinutes = reminderMinutes,
    repeat = repeat
)

data class PlannerEventUi(
    val id: Long,
    val title: String,
    val type: EventType,
    val dateTime: String,
    val reminderMinutes: Int? = null,
    val repeat: String? = null
)

class PlannerVm(private val repo: PlannerRepository) : ViewModel() {
    private val _events = MutableStateFlow<List<PlannerEventUi>>(emptyList())
    val events: StateFlow<List<PlannerEventUi>> = _events

    init {
        viewModelScope.launch {
            repo.events().map { list -> list.map { it.toUi() } }.collect { _events.value = it }
        }
    }

    fun add(title: String, type: EventType, dateTime: String, reminder: Int?, repeat: String?) {
        viewModelScope.launch {
            repo.addEvent(title, type.name, dateTime, reminder, repeat)
        }
    }
}

@Composable
fun PlannerScreen() {
    val context = LocalContext.current
    val vm: PlannerVm = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PlannerVm(PlannerRepository(context)) as T
        }
    })
    val events by vm.events.collectAsState()
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(EventType.MEDS) }
    var dateTime by remember { mutableStateOf("") }
    var reminder by remember { mutableStateOf<Int?>(15) }
    var repeat by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Планировщик")
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        OutlinedTextField(value = dateTime, onValueChange = { dateTime = it }, label = { Text("Дата и время (строка)") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EventType.values().forEach { t ->
                FilterChip(selected = type == t, onClick = { type = t }, label = { Text(t.label) })
            }
        }
        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(15, 60, 24*60).forEach { mins ->
                FilterChip(selected = reminder == mins, onClick = { reminder = mins }, label = { Text("Напоминание: ${mins} мин") })
            }
            FilterChip(selected = reminder == null, onClick = { reminder = null }, label = { Text("Без напомин.") })
        }
        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Ежедневно", "Еженедельно").forEach { r ->
                FilterChip(selected = repeat == r, onClick = { repeat = r }, label = { Text(r) })
            }
            FilterChip(selected = repeat == null, onClick = { repeat = null }, label = { Text("Без повтора") })
        }
        Button(onClick = {
            if (title.isNotBlank()) {
                vm.add(title, type, dateTime, reminder, repeat)
                title = ""
            }
        }, modifier = Modifier.padding(top = 8.dp)) { Text("Добавить") }

        LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
            items(events) { e ->
                ElevatedCard(modifier = Modifier.padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(e.title, style = MaterialTheme.typography.titleMedium)
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color = e.type.color, shape = CircleShape)
                                    .padding(end = 8.dp)
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(end = 8.dp))
                            Text("${e.type.label} | ${e.dateTime}")
                        }
                        Text("Напоминание: ${e.reminderMinutes ?: 0} мин, Повтор: ${e.repeat ?: "нет"}")
                    }
                }
            }
        }
    }
}


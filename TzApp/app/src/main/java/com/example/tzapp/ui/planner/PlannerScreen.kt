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
import androidx.compose.material3.TextButton
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
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.flow.map
import com.example.tzapp.data.planner.PlannerRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tzapp.data.planner.PlannerEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.example.tzapp.work.ReminderWorker
import java.util.concurrent.TimeUnit
import android.widget.Toast
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions

enum class EventType(val label: String, val color: Color) {
    MEDS("Лекарства", Color(0xFF2962FF)),
    DOCTOR("Врачи", Color(0xFFFF1744)),
    EVENT("Мероприятия", Color(0xFF2E7D32))
}

private fun com.example.tzapp.data.planner.PlannerEventEntity.toUi(): PlannerEventUi = PlannerEventUi(
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
fun PlannerScreen(onGoHome: (() -> Unit)? = null) {
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Планировщик", style = MaterialTheme.typography.titleLarge)
            if (onGoHome != null) {
                TextButton(onClick = onGoHome) { Text("На главную") }
            }
        }
        // Простой месячный календарь с точками
        val cal = remember { Calendar.getInstance() }
        val daysInMonth = remember(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)) {
            cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        // Парсер дат для событий
        fun parseDate(text: String): Date? {
            val patterns = listOf(
                "dd.MM.yyyy HH:mm",
                "dd.MM.yyyy",
                "dd/MM/yyyy HH:mm",
                "dd/MM/yyyy"
            )
            for (p in patterns) {
                try {
                    val sdf = SimpleDateFormat(p, Locale.getDefault())
                    sdf.isLenient = false
                    return sdf.parse(text)
                } catch (_: Exception) {}
            }
            return null
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
            Text(SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(Date()), style = MaterialTheme.typography.titleMedium)
        }
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(7), modifier = Modifier.padding(top = 8.dp)) {
            items(daysInMonth) { idx ->
                val day = idx + 1
                val dayEvents = events.filter { e ->
                    val d = parseDate(e.dateTime)
                    if (d != null) {
                        val c = Calendar.getInstance().apply { time = d }
                        c.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                            c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
                            c.get(Calendar.DAY_OF_MONTH) == day
                    } else false
                }
                val dominantType = dayEvents.groupBy { it.type }.maxByOrNull { it.value.size }?.key
                val bgColor = dominantType?.color?.copy(alpha = 0.18f) ?: Color.Transparent
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color = bgColor, shape = RoundedCornerShape(6.dp))
                        .padding(6.dp)
                ) {
                    Column {
                        Text(day.toString())
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            dayEvents.take(3).forEach { e ->
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier.size(6.dp).background(e.type.color, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название (русский текст поддерживается)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = dateTime,
            onValueChange = { dateTime = it },
            label = { Text("Дата и время (напр. 25.08.2025 14:30)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
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
                // Планирование уведомления
                reminder?.let { mins ->
                    val eventTime = parseDate(dateTime)?.time ?: System.currentTimeMillis()
                    val triggerAt = eventTime - mins * 60_000L
                    val delay = (triggerAt - System.currentTimeMillis()).coerceAtLeast(0L)
                    val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(
                            workDataOf(
                                ReminderWorker.KEY_TITLE to title,
                                ReminderWorker.KEY_TEXT to "${type.label} | ${dateTime}",
                                ReminderWorker.KEY_ID to (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
                            )
                        ).build()
                    WorkManager.getInstance(context).enqueue(work)
                }
                Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
                title = ""
            }
        }, modifier = Modifier.padding(top = 8.dp)) { Text("Сохранить") }

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
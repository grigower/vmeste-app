package com.example.tzapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
	var notificationsEnabled by remember { mutableStateOf(true) }
	var analyticsEnabled by remember { mutableStateOf(true) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text("Настройки")
		SettingItem(label = "Уведомления", checked = notificationsEnabled) {
			notificationsEnabled = it
		}
		SettingItem(label = "Аналитика", checked = analyticsEnabled) {
			analyticsEnabled = it
		}
	}
}

@Composable
private fun SettingItem(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
	Column {
		Text(text = label)
		Switch(checked = checked, onCheckedChange = onChange)
	}
}


package com.example.tzapp.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tzapp.data.AuthManager

@Composable
fun LoginScreen(onSuccess: () -> Unit) {
	var email by remember { mutableStateOf("") }
	var password by remember { mutableStateOf("") }
	var error by remember { mutableStateOf<String?>(null) }

	Column(modifier = Modifier.padding(24.dp)) {
		Text(text = "Вход")
		OutlinedTextField(
			value = email,
			onValueChange = { email = it },
			label = { Text("Email") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 12.dp)
		)
		OutlinedTextField(
			value = password,
			onValueChange = { password = it },
			label = { Text("Пароль") },
			visualTransformation = PasswordVisualTransformation(),
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 12.dp)
		)
		if (error != null) {
			Text(text = error!!, modifier = Modifier.padding(top = 8.dp))
		}
		Button(
			onClick = {
				val ok = AuthManager.login(email.trim(), password)
				if (ok) onSuccess() else error = "Неверные данные"
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp)
		) {
			Text("Войти")
		}
	}
}


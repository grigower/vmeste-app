package com.example.tzapp.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tzapp.data.AuthManager

@Composable
fun ProfileScreen(onRequestLogin: () -> Unit, onLogout: () -> Unit) {
	val user = AuthManager.currentUser
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp)
	) {
		if (user == null) {
			Text("Вы не авторизованы")
			Button(
				onClick = onRequestLogin,
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp)
			) { Text("Войти") }
		} else {
			Text("Профиль")
			Text("Имя: ${user.displayName}", modifier = Modifier.padding(top = 8.dp))
			Text("Email: ${user.email}")
			Button(
				onClick = {
					AuthManager.logout()
					onLogout()
				},
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp)
			) { Text("Выйти") }
		}
	}
}


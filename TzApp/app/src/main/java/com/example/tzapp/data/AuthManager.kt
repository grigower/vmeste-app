package com.example.tzapp.data

data class User(
	val email: String,
	val displayName: String
)

object AuthManager {
	@Volatile
	var currentUser: User? = null
		private set

	fun isAuthenticated(): Boolean = currentUser != null

	fun login(email: String, password: String): Boolean {
		if (email.isBlank() || password.length < 4) return false
		val namePart = email.substringBefore('@').ifBlank { "User" }
		currentUser = User(email = email, displayName = namePart.replaceFirstChar { it.uppercase() })
		return true
	}

	fun logout() {
		currentUser = null
	}
}


package com.example.tzapp.data

object Repository {
	val items: List<Item> = List(20) { index ->
		Item(
			id = index + 1,
			title = "Элемент #${index + 1}",
			description = "Описание элемента #${index + 1}. Это демонстрационные данные."
		)
	}

	fun getItemById(id: Int): Item? = items.find { it.id == id }
}


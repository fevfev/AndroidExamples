package com.knyazev.notesvault.data

import androidx.room.PrimaryKey

@androidx.room.Entity(tableName = "notes") // Имя таблицы в БД
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Primary key, автогенерация ID
    val title: String, // Заголовок заметки
    val content: String, // Содержание заметки
    val category: String = "Без категории", // Категория заметки, по умолчанию "Без категории"
    val createdAt: Long = System.currentTimeMillis() // Время создания
)
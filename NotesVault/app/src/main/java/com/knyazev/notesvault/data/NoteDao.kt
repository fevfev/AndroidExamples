package com.knyazev.notesvault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao // Аннотация Room, указывает, что это DAO
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Добавить новую заметку, REPLACE - заменять при конфликте PK (для update)
    suspend fun insert(note: Note) // Функция для добавления заметки

    @Update // Обновить заметку
    suspend fun update(note: Note) // Функция для обновления заметки

    @Delete // Удалить заметку
    suspend fun delete(note: Note) // Функция для удаления заметки

    @Query("SELECT * FROM notes WHERE id = :id") // Запрос к БД
    fun getNoteById(id: Long): Flow<Note?> // Функция для получения заметкит по ID

    @Query("SELECT * FROM notes ORDER BY title ASC") // Функция для получения всех заметок
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT DISTINCT category FROM notes ORDER BY category ASC") // Функция для получения уникальных категорий
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY title ASC") // Функция для поиска заметок
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY title ASC") // Функция для получения заметок по категории
    fun getNotesByCategory(category: String): Flow<List<Note>>
}
package com.knyazev.notesvault.ui.screens

import android.app.Application
import androidx.lifecycle.*
import com.knyazev.notesvault.data.Note
import com.knyazev.notesvault.data.NoteDao
import com.knyazev.notesvault.data.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao: NoteDao // DAO для работы с БД
    val allNotes: LiveData<List<Note>> // Список всех заметок
    val allCategories: LiveData<List<String>> // Список всех категорий


    private val _searchQuery = MutableLiveData<String>("") // Стартовый запрос поиска - пустая строка
    val searchQuery: LiveData<String> = _searchQuery

    private val _selectedCategory = MutableLiveData<String>("Все") // Стандартный фильтр для всех
    val selectedCategory: LiveData<String> = _selectedCategory


    val categoryFilteredNotes: LiveData<List<Note>> = _selectedCategory.switchMap { category: String -> // Используем switchMap как функцию расширения LiveData
        if (category == "Все") {
            allNotes
        } else {
            noteDao.getNotesByCategory(category).asLiveData() // Получение заметок по категории
        }
    }



    val searchedNotes: LiveData<List<Note>> = _searchQuery.switchMap { query: String? -> // Используем switchMap как функцию расширения LiveData
        if (query.isNullOrEmpty()) {
            categoryFilteredNotes
        } else {
            noteDao.searchNotes(query).asLiveData() // Поиск заметок
        }
    }


    init {
        val database = NoteDatabase.getDatabase(application) // Получение экземпляра базы данных
        noteDao = database.noteDao() // Получение DAO
        allNotes = noteDao.getAllNotes().asLiveData() // Получение всех заметок
        allCategories = noteDao.getAllCategories().asLiveData() // Получение всех категорий
    }

    // CRUD операции (выполняются в IO dispatcher)
    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) { // Добавление заметки
        noteDao.insert(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) { // Обновление заметки
        noteDao.update(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) { // Удаление заметки
        noteDao.delete(note)
    }

    fun getNoteById(id: Long): LiveData<Note?> { // Получение заметки по ID
        return noteDao.getNoteById(id).asLiveData()
    }



    fun setSearchQuery(query: String) { // Установка запроса поиска
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) { // Установка выбранной категории
        _selectedCategory.value = category
    }


    class NoteViewModelFactory(private val application: Application) : ViewModelProvider.Factory { // Фабрика для создания ViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T { // Создание ViewModel
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") // Подавление предупреждения
                return NoteViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class") // Ошибка, если класс ViewModel неизвестен
        }
    }
}
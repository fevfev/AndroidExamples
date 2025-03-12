package com.knyazev.notesvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.knyazev.notesvault.data.Note
import com.knyazev.notesvault.data.SettingsManager
import com.knyazev.notesvault.ui.screens.NoteViewModel
import com.knyazev.notesvault.ui.theme.NotesVaultTheme

class MainActivity : ComponentActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var noteViewModel: NoteViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Включаем режим "от края до края"
        super.onCreate(savedInstanceState)

        settingsManager = SettingsManager(this)
        settingsManager.applyCurrentTheme() // Применяем текущую тему

        noteViewModel = ViewModelProvider(
            this,
            NoteViewModel.NoteViewModelFactory(application)
        )[NoteViewModel::class.java]

        setContent {
            val isDarkTheme = settingsManager.getThemeMode() == SettingsManager.THEME_DARK
            var themeSwitcherState by remember { mutableStateOf(isDarkTheme) }
            val notes by noteViewModel.categoryFilteredNotes.observeAsState(initial = emptyList()) // Наблюдаем за списком заметок, отфильтрованных по категории
            var showDialog by remember { mutableStateOf(false) } // Состояние для отображения диалога добавления/редактирования
            var editingNote by remember { mutableStateOf<Note?>(null) } // Состояние для редактируемой заметки
            var searchQuery by remember { mutableStateOf("") } // Состояние для строки поиска
            val categories by noteViewModel.allCategories.observeAsState(initial = emptyList()) // Наблюдаем за списком категорий
            var selectedCategoryFilter by remember { mutableStateOf("Все") } // Состояние для выбранного фильтра категории

            NotesVaultTheme(darkTheme = themeSwitcherState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Заметки") },
                                actions = {
                                    IconButton(onClick = { showDialog = true; editingNote = null }) { // Кнопка "Добавить"
                                        Icon(Icons.Filled.Add, "Добавить заметку")
                                    }
                                    ThemeSwitcher(
                                        themeSwitcherState = themeSwitcherState,
                                        onThemeChange = { isChecked ->
                                            themeSwitcherState = isChecked
                                            val themeMode = when {
                                                isChecked -> SettingsManager.THEME_DARK
                                                else -> SettingsManager.THEME_LIGHT
                                            }
                                            settingsManager.setThemeMode(themeMode)
                                        }
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                )
                            )
                        },
                        floatingActionButton = {
                            // floatingActionButton для добавления заметки (альтернатива кнопке в TopAppBar)
                            FloatingActionButton(onClick = { showDialog = true; editingNote = null }) {
                                Icon(Icons.Filled.Add, "Добавить заметку")
                            }
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp)
                        ) {
                            // Поле поиска
                            TextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                    noteViewModel.setSearchQuery(it)
                                },
                                label = { Text("Поиск заметок") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                                )
                            )

                            // Фильтр по категориям
                            CategoryFilter(
                                categories = noteViewModel.allCategories,
                                selectedCategory = selectedCategoryFilter,
                                onCategoryChange = {
                                    selectedCategoryFilter = it
                                    noteViewModel.setSelectedCategory(it)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Список заметок
                            NoteList(
                                notes = notes,
                                onEdit = { note ->
                                    editingNote = note
                                    showDialog = true
                                },
                                onDelete = { note ->
                                    noteViewModel.deleteNote(note)
                                }
                            )
                        }

                        // Диалог добавления/редактирования заметки
                        if (showDialog) {
                            NoteDialog(
                                onDismissRequest = { showDialog = false },
                                onConfirm = { note ->
                                    if (editingNote != null) {
                                        noteViewModel.updateNote(note.copy(id = editingNote!!.id)) // Обновление существующей заметки
                                    } else {
                                        noteViewModel.insertNote(note) // Добавление новой заметки
                                    }
                                    showDialog = false
                                    editingNote = null
                                },
                                note = editingNote // Передаем заметку для редактирования, если есть
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeSwitcher(themeSwitcherState: Boolean, onThemeChange: (Boolean) -> Unit) {
    Switch(
        checked = themeSwitcherState,
        onCheckedChange = onThemeChange,
        modifier = Modifier.padding(8.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilter(categories: LiveData<List<String>>, selectedCategory: String, onCategoryChange: (String) -> Unit) {
    val categoryList by categories.observeAsState(initial = emptyList())
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = selectedCategory,
            onValueChange = {},
            label = { Text("Категория") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Все") },
                onClick = {
                    onCategoryChange("Все")
                    expanded = false
                }
            )
            categoryList.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategoryChange(category)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun NoteList(notes: List<Note>, onDelete: (Note) -> Unit, onEdit: (Note) -> Unit) {
    if (notes.isEmpty()) {
        Text("Нет заметок. Добавьте новую заметку!", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
    } else {
        LazyColumn {
            items(notes) { note ->
                NoteListItem(note = note, onDelete = onDelete, onEdit = onEdit)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun NoteListItem(note: Note, onDelete: (Note) -> Unit, onEdit: (Note) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onEdit(note) }, // Редактирование по клику на элемент списка
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Равномерное распределение элементов
    ) {
        Column(modifier = Modifier.weight(1f)) { // Занимает большую часть пространства
            Text(text = note.title, style = MaterialTheme.typography.titleMedium)
            Text(text = note.content, maxLines = 1, style = MaterialTheme.typography.bodySmall) // maxLines для краткости
            Text(text = "Категория: ${note.category}", style = MaterialTheme.typography.labelSmall)
        }
        IconButton(onClick = { onDelete(note) }) { // Кнопка удаления справа
            Icon(Icons.Filled.Delete, "Удалить заметку")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDialog(onDismissRequest: () -> Unit, onConfirm: (Note) -> Unit, note: Note?) {
    var title by remember { mutableStateOf(note?.title ?: "Заголовок") }
    var content by remember { mutableStateOf(note?.content ?: "Введите текст") }
    var category by remember { mutableStateOf(note?.category ?: "Основые") }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    val categories = listOf("Основые", "Работа", "Личное", "Идеи", "Путешествия") // Пример категорий

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                onConfirm(Note(title = title, content = content, category = category))
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text("Отмена")
            }
        },
        title = { Text(if (note == null) "Добавить заметку" else "Редактировать заметку") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    )
                )
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Содержимое") },
                    modifier = Modifier.fillMaxWidth().height(150.dp).padding(bottom = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategoryDropdown,
                    onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
                ) {
                    TextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Категория") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoryDropdown,
                        onDismissRequest = { expandedCategoryDropdown = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCategoryDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotesVaultTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Предпросмотр списка заметок")
                NoteList(notes = listOf(
                    Note(title = "Тест 1", content = "Тест заметки 1", category = "Личное"),
                    Note(title = "Тест 2", content = "Тест заметки 2", category = "Работа")
                ), onDelete = {}, onEdit = {})
            }

        }
    }
}
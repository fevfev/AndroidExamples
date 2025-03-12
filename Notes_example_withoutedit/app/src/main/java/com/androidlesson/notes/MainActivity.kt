package com.androidlesson.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidlesson.notes.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesTheme {
                NotesApp()
            }
        }
    }
}

@Composable
fun NotesApp() {
    // Создаем список заметок, который будет хранить все добавленные заметки
    val notes = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Устанавливаем фоновый цвет
            .padding(16.dp) // Добавляем отступы
    ) {
        // Ввод заметки и добавление ее в список
        NoteInput(onNoteAdded = { note: String -> notes.add(0, note) })
        Spacer(modifier = Modifier.height(16.dp)) // Добавляем вертикальный отступ
        // Отображаем список заметок
        NotesList(notes = notes)
    }
}

@Composable
fun NotesList(notes: MutableList<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Добавляем отступы
    ) {
        // Проходим по списку заметок и отображаем каждую заметку
        itemsIndexed(notes) { index, note ->
            NoteItem(note = note) {
                notes.removeAt(index) // Удаляем заметку при нажатии на иконку удаления
            }
        }
    }
}

@Composable
fun NoteItem(note: String, onDelete: () -> Unit) {
    var showDeleteIcon by remember { mutableStateOf(false) }
    // Анимируем цвет фона при долгом нажатии
    val animatedColor by animateColorAsState(
        targetValue = if (showDeleteIcon) Color.Red.copy(alpha = 0.1f) else Color.Transparent,
        label = ""
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(animatedColor)
            .padding(vertical = 4.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showDeleteIcon = true } // Показываем иконку удаления при долгом нажатии
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Отображаем текст заметки
            Text(
                text = note,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // Иконка удаления заметки
            IconButton(
                onClick = onDelete,
                modifier = Modifier.alpha(if (showDeleteIcon) 1f else 0f) // Прозрачность иконки
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

@Preview
@Composable
fun NoteInputPreview() {
    NoteInput(onNoteAdded = {})
}

@Composable
fun NoteInput(onNoteAdded: (String) -> Unit) {
    // Создаем состояние для текста заметки
    val noteText = remember { mutableStateOf("") }

    TextField(
        value = noteText.value,
        onValueChange = { noteText.value = it }, // Обновляем текст заметки при вводе
        label = { NoteLabel() }, // Метка для поля ввода
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Добавляем отступы
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text) // Настройки клавиатуры
    )
    Button(
        onClick = {
            if (noteText.value.isNotBlank()) {
                onNoteAdded(noteText.value) // Добавляем заметку
                noteText.value = "" // Очищаем поле ввода после добавления заметки
            }
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text("Добавить заметку")
    }
}

@Composable
fun NoteLabel() {
    Text("Введите заметку")
}
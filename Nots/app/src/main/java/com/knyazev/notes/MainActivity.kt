package com.knyazev.notes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.knyazev.notes.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesApp()
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun NotesApp() {
    var notes by remember { mutableStateOf(sampleNotes().toMutableList()) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить заметку")
            }
        },
        bottomBar = { BottomMenu() }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NotesList(notes)
            if (showDialog) {
                AddNoteDialog(
                    onAdd = { note ->
                        notes.add(0, note)
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun NotesList(notes: List<Note>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(notes.size) { i ->
            NoteCard(note = notes[i])
        }
    }
}

@Composable
fun NoteCard(note: Note) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = note.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp)
            )
            Column {
                Text(note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(note.description, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    note.category,
                    color = Color(0xFF2EB4FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AddNoteDialog(onAdd: (Note) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF5F5F5)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Новая заметка",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF2EB4FF)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Заголовок") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория") },
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена", color = Color(0xFF2EB4FF))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(
                                    Note(
                                        title = title,
                                        description = description,
                                        category = category,
                                        imageRes = R.drawable.ic_note
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2EB4FF),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }
}

@Composable
fun BottomMenu() {
    NavigationBar(
        containerColor = Color(0xFFF5F5F5), // Светлый фон
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* TODO */ },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = "Главная",
                    modifier = Modifier.size(20.dp), // Уменьшенный размер
                    tint = Color(0xFF2EB4FF) // Синий цвет
                )
            },
            label = {
                Text(
                    "Главная",
                    color = Color(0xFF2EB4FF),
                    fontSize = 12.sp
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_notes),
                    contentDescription = "Заметки",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF2EB4FF)
                )
            },
            label = {
                Text(
                    "Заметки",
                    color = Color(0xFF2EB4FF),
                    fontSize = 12.sp
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = {  },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "Настройки",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF2EB4FF)
                )
            },
            label = {
                Text(
                    "Настройки",
                    color = Color(0xFF2EB4FF),
                    fontSize = 12.sp
                )
            }
        )
    }
}

// Пример начальных заметок
fun sampleNotes() = listOf(
    Note(
        title = "Учёба",
        description = "Подготовить доклад по Android",
        category = "Важное",
        imageRes = R.drawable.ic_note // замените на свою иконку
    ),
    Note(
        title = "Покупки",
        description = "Купить молоко и хлеб",
        category = "Быт",
        imageRes = R.drawable.ic_note
    )
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesTheme {
        NotesApp()
    }
}
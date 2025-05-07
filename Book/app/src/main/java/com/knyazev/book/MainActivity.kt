package com.knyazev.book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knyazev.book.ui.theme.BookTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookTheme {
                BookApp()
            }
        }
    }
}

// Главное приложение с навигацией
@Composable
fun BookApp() {
    val navController = rememberNavController()
    val books = remember { mutableStateListOf(*getSampleBooks().toTypedArray()) }

    NavHost(navController = navController, startDestination = "bookList") {
        composable("bookList") {
            BookListScreen(books, navController)
        }
        composable("bookDetail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull() ?: 1
            BookDetailScreen(
                book = books.find { it.id == bookId } ?: books[0],
                onBackClick = { navController.popBackStack() },
                onFavoriteClick = { book ->
                    val index = books.indexOfFirst { it.id == book.id }
                    if (index != -1) {
                        books[index] = book.copy(isFavorite = !book.isFavorite)
                    }
                }
            )
        }
    }
}

// Экран списка книг
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(books: MutableList<Book>, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Книжная полка") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                BookCard(
                    book = book,
                    onCardClick = { navController.navigate("bookDetail/${book.id}") },
                    onFavoriteClick = {
                        val index = books.indexOfFirst { it.id == book.id }
                        if (index != -1) {
                            books[index] = book.copy(isFavorite = !book.isFavorite)                        }
                    }
                )
            }
        }
    }
}

// Карточка книги с анимацией
@Composable
fun BookCard(
    book: Book,
    onCardClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    // Анимация пульсации иконки избранного
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val heartSize by infiniteTransition.animateFloat(
        initialValue = 24f,
        targetValue = if (book.isFavorite) 30f else 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )

    val favoriteColor by animateColorAsState(
        targetValue = if (book.isFavorite) Color.Red else Color.Gray,
        label = "favorite color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Изображение книги
            Image(
                painter = painterResource(id = book.imageRes),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onCardClick() },
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Заголовок и автор
                    Column(modifier = Modifier
                        .weight(1f)
                        .clickable { onCardClick() }
                    ) {
                        Text(
                            text = book.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = book.author,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    // Иконка избранного с анимацией
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Добавить в избранное",
                            tint = favoriteColor,
                            modifier = Modifier.size(heartSize.dp)
                        )
                    }
                }

                // Короткое описание
                Text(
                    text = book.shortDescription,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Кнопка "Подробнее" с анимацией стрелки
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Подробнее",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Развернуть",
                        modifier = Modifier.rotate(rotationState)
                    )
                }

                // Анимированное появление полного описания
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = book.fullDescription,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

// Экран с детальной информацией о книге
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book,
    onBackClick: () -> Unit,
    onFavoriteClick: (Book) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    // Анимированная иконка избранного
                    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

                    val heartSize by infiniteTransition.animateFloat(
                        initialValue = 24f,
                        targetValue = if (book.isFavorite) 30f else 24f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "heartbeat"
                    )

                    val favoriteColor by animateColorAsState(
                        targetValue = if (book.isFavorite) Color.Red else Color.Gray,
                        label = "favorite color"
                    )

                    IconButton(onClick = { onFavoriteClick(book) }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Добавить в избранное",
                            tint = favoriteColor,
                            modifier = Modifier.size(heartSize.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Изображение книги
            Image(
                painter = painterResource(id = book.imageRes),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Заголовок и автор
            Text(
                text = book.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = book.author,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // Полное описание
            Text(
                text = "О книге:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.fullDescription,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }
    }
}

// Примеры книг
fun getSampleBooks(): List<Book> {
    return listOf(
        Book(
            id = 1,
            title = "Мастер и Маргарита",
            author = "Михаил Булгаков",
            shortDescription = "Классический роман о добре и зле, о визите дьявола в Москву.",
            fullDescription = "«Мастер и Маргарита» - роман Михаила Булгакова, работа над которым началась в 1928 году и продолжалась вплоть до смерти писателя. Роман относится к незавершённым произведениям; редактирование рукописи романа Булгаков закончил за месяц до смерти, в феврале 1940 года.",
            imageRes = R.drawable.book1
        ),
        Book(
            id = 2,
            title = "Преступление и наказание",
            author = "Фёдор Достоевский",
            shortDescription = "Психологический роман о молодом человеке, совершившем преступление.",
            fullDescription = "«Преступление и наказание» - социально-психологический и социально-философский роман Фёдора Михайловича Достоевского, над которым писатель работал в 1865-1866 годах. Впервые опубликован в 1866 году в журнале «Русский вестник».",
            imageRes = R.drawable.book2
        ),
        Book(
            id = 3,
            title = "Война и мир",
            author = "Лев Толстой",
            shortDescription = "Эпическая сага о жизни русского дворянства в эпоху наполеоновских войн.",
            fullDescription = "«Война и мир» - роман-эпопея Льва Николаевича Толстого, описывающий русское общество в эпоху войн против Наполеона в 1805-1812 годах. Эпопея состоит из четырёх томов. Писатель работал над книгой в 1863-1869 годах.",
            imageRes = R.drawable.book3
        )
    )
}
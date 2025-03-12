package com.androidlesson.imagegallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidlesson.imagegallery.ui.theme.ImageGalleryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageGalleryTheme {
                ImageGallery()
            }
        }
    }
}

// Список идентификаторов ресурсов изображений
val imageResIds = listOf(
    R.drawable.image0,
    R.drawable.image1,
    R.drawable.image2,
    R.drawable.image3,
    R.drawable.image4,
    R.drawable.image5,
    R.drawable.image6,
    R.drawable.image7,
    R.drawable.image8,
    R.drawable.image9,
    // Добавьте все используемые изображения сюда (image10, image11, ... не забудьте перед этим добавить их в папку res/drawable
)

@Composable
fun ImageGallery() {
    // Создание сетки изображений
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Установка двух колонок
        contentPadding = PaddingValues(8.dp), // Отступы вокруг содержимого
        modifier = Modifier.fillMaxSize() // Заполнение всего доступного пространства
    ) {
        // Отображение каждого изображения из списка
        items(imageResIds.size) { index ->
            ImageCard(imageResIds[index])
        }
    }
}

@Composable
fun ImageCard(imageResId: Int) {
    // Карточка для отображения изображения
    Card(
        modifier = Modifier
            .padding(8.dp) // Отступы вокруг карточки
            .aspectRatio(1f), // Соотношение сторон 1:1
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Высота тени
    ) {
        Box(
            modifier = Modifier
                .background(Color.Gray) // Фон серого цвета
                .fillMaxSize(), // Заполнение всего доступного пространства
            contentAlignment = Alignment.Center // Центрирование содержимого
        ) {
            // Отображение изображения
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Image $imageResId" // Описание изображения
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageGalleryExample() {
    // Пример предварительного просмотра галереи изображений
    ImageGalleryTheme {
        ImageGallery()
    }
}
package com.knyazev.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherApp()
        }
    }
}

@Composable
fun WeatherApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            WeatherBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF4F8FB))
        ) {
            WeatherContent()
        }
    }
}

@Composable
fun WeatherContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок с названием города
        CityHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // Основной блок с изображением и данными погоды
        WeatherMainCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Детальная информация о погоде
        WeatherDetailsCard()
    }
}

@Composable
fun CityHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Местоположение",
                tint = Color(0xFF5B8DEF)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Йошкар-Ола",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        Text(
            text = "Сегодня, 5 мая",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun WeatherMainCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "cloud_animation")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_movement"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Фоновое изображение города
        Image(
            painter = painterResource(id = R.drawable.yoshkar_ola_city),
            contentDescription = "Город",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Полупрозрачный оверлей для лучшей читаемости текста
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x88000000))
        )

        // Анимированное облако (использует cloudOffset для движения)
        Image(
            painter = painterResource(id = R.drawable.ic_partly_cloudy),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .offset(x = (cloudOffset - 40).dp, y = (-cloudOffset / 2).dp)
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .zIndex(2f)
        )

        // Информация о погоде поверх изображения
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Иконка погоды с описанием
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_partly_cloudy),
                    contentDescription = "Погода",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Переменная облачность",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            // Большая цифра температуры
            Text(
                text = "18°",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun WeatherDetailsCard() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Подробная информация",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(
                    icon = painterResource(id = R.drawable.water),
                    title = "Влажность",
                    value = "65%"
                )

                WeatherDetail(
                    icon = painterResource(id = R.drawable.air),
                    title = "Ветер",
                    value = "5 м/с"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetail(
                    icon = painterResource(id = R.drawable.fog),
                    title = "Видимость",
                    value = "10 км"
                )

                WeatherDetail(
                    icon = painterResource(id = R.drawable.pl),
                    title = "Давление",
                    value = "756 мм"
                )
            }
        }
    }
}

@Composable
fun WeatherDetail(icon: Painter, title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(140.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = title,
            tint = Color(0xFF5B8DEF),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WeatherBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = { Icon(Icons.Outlined.LocationOn, contentDescription = "День") },
            label = { Text("День") }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = { Icon(Icons.Outlined.DateRange, contentDescription = "Неделя") },
            label = { Text("Неделя") }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = { Icon(Icons.Outlined.Menu, contentDescription = "Месяц") },
            label = { Text("Месяц") }
        )
    }
}
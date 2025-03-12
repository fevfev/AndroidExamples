package com.education.myapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.education.myapp.ui.theme.MyAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppTheme {
                ExpandableCard("Анимированная карточка","Для упрощения выбора подходящей анимации на данной схеме представлена рекомендованная Google схема выбора подходящей анимации для пользовательских компонентов.")            }
        }
    }
}
@Composable
fun ExpandableCard(
    title: String,
    content: String
) {
    var expanded by remember { mutableStateOf(false) }
    val height by animateDpAsState(
        targetValue = if (expanded) 200.dp else 80.dp
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable { expanded = !expanded }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {
                    pressed = true
                    onClick()
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    pressed = true
                    tryAwaitRelease()
                    pressed = false
                })
            },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun TransitionAnimationExample() {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(expanded) {
            Card(modifier = Modifier.padding(16.dp)) {
                Text("Скрытый контент", modifier = Modifier.padding(16.dp))
            }
        }
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, "Показать")
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun MyApp() {
    MaterialTheme {
        Column {
         //   Greeting("Анна")
            AnimatedCustomButton()
        }
    }
}
@Composable
fun AnimatedButton() {
    var expanded by remember { mutableStateOf(false) }
    Button(
        onClick = { expanded = !expanded },
        modifier = Modifier.animateContentSize()
    ) {
        Text(if (expanded) "Свернуть" else "Развернуть")
    }
}

@Composable
fun TaskApp() {
    val tasks = remember { mutableStateListOf("Задача 1", "Задача 2") }
    Column {
        tasks.forEach { task ->
            Text(text = task)
        }
        Button(onClick = { tasks.add("Новая задача") }) {
            Text("Добавить задачу")
        }
    }
}
@Composable
fun IconButtonCustom(text: String, icon: ImageVector) {
    Button(onClick = {}) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text)
        }
    }
}
@Composable
fun AlertDialogCustom() {
    var openDialog by remember { mutableStateOf(false) }
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text("Заголовок") },
            text = { Text("Текст") },
            confirmButton = {
                Button(
                    onClick = { openDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
    Button(onClick = { openDialog = true }) {
        Text("Показать диалог")
    }
}
@Composable
fun CardCustom() {
    Card(shape = RoundedCornerShape(8.dp)) {
        Text("Контент карточки", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun AnimatedCustomButton(){
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(expanded) {
            Card(modifier = Modifier.padding(16.dp)) {
                Text("Скрытый контент", modifier = Modifier.padding(16.dp))
            }
        }
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Default.Add, "Показать")
        }
    }
}
fun Modifier.neuromorphicShadow(
    lightShadowColor: Color = Color.White.copy(alpha = 0.8f),
    darkShadowColor: Color = Color.Black.copy(alpha = 0.1f),
    cornerRadius: Dp = 16.dp,
    shadowElevation: Dp = 10.dp
): Modifier = this
    // Верхняя светлая тень
    .shadow(
        elevation = shadowElevation,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = lightShadowColor,
        spotColor = lightShadowColor
    )
    // Дополнительная тень снизу
    .shadow(
        elevation = shadowElevation,
        shape = RoundedCornerShape(cornerRadius),
        ambientColor = darkShadowColor,
        spotColor = darkShadowColor
    )
@Composable
fun SmartHomeDashboardScreen() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column {
                Spacer(modifier = Modifier.height(32.dp))
                NeuromorphicButton(text = "Включить свет") {
                    // Логика управления устройством
                }
                Spacer(modifier = Modifier.height(16.dp))
                NeuromorphicButton(text = "Изменить температуру") {
                    // Логика управления устройством
                }
                // Другие элементы интерфейса...
            }
        }
    }
}

@Composable
fun NeuromorphicButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(150.dp, 60.dp)
            .background(
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .neuromorphicShadow()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 18.sp, color = Color.DarkGray)
    }
}
@Composable
fun LayoutExample() {
    // Горизонтальное размещение
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Левый")
        Text("Центр")
        Text("Правый")
    }

    // Наложение элементов
    Box(modifier = Modifier.size(100.dp)) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            "Поверх картинки",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun cioexample() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            "30%",
            modifier = Modifier
                .weight(0.3f)
                .background(Color.LightGray)
        )
        Text(
            "70%",
            modifier = Modifier
                .weight(0.7f)
                .background(Color.Gray)
        )
    }
}

@Composable
fun PaddingExample() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp) // отступы снаружи
            .background(Color.LightGray)
            .padding(horizontal = 8.dp)  // отступы внутри
    ) {
        Text("Текст с отступами")
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ResponsiveLayout() {
    BoxWithConstraints {
        when {
            maxWidth < 600.dp -> PaddingExample()
            else -> cioexample()
        }
    }
}
@Composable
fun ProductCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.bread),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    "Хлеб",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Хлеб ржаной",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Состав: мука, вода, соль, дрожжи",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(onClick = { /* Добавить в корзину */ }, ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Добавить в корзину", tint = Color(
                        0xFF794231
                    )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(onClick = { /* Поставить лайк */ }) {
                    Icon(Icons.Default.Favorite, contentDescription = "В избранное", tint = Color(
                        0xFFEC2273
                    )
                    )
                }
            }
        }
    }
}
@Composable
fun CustomCard(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
@Composable
fun Modifier.customBorder() = composed {
    this
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp)
        )
        .padding(2.dp)
}


@Composable
fun MD3Card(
    title: String,
    content: String
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
fun animationvis(){
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally(),
        exit = fadeOut() + slideOutHorizontally()
    ) {
        Text("Анимированный текст")
    }
}
@Composable
fun AnimateSizeExample() {
    var size by remember { mutableStateOf(100.dp) }
    val animatedSize by animateDpAsState(
        targetValue = size,
        animationSpec = tween(durationMillis = 1000)
    )

    Column {
        Button(onClick = { size = if (size == 100.dp) 200.dp else 100.dp }) {
            Text("Изменить размер")
        }
        Box(Modifier.size(animatedSize).background(Color.Blue))
    }
}
@Composable
fun ModernGreeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Привет $name!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ты начинаешь свой путь!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun SequenceAnimationExample() {
    var size by remember { mutableStateOf(100.dp) }
    val animatedSize by animateDpAsState(
        targetValue = size,
        animationSpec = keyframes {
            durationMillis = 3000
            100.dp at 0 using LinearEasing // Начинаем с 100.dp
            200.dp at 1500 using FastOutSlowInEasing // Переходим к 200.dp через 1.5 секунды
            150.dp at 3000 using LinearEasing // Заканчиваем на 150.dp
        }
    )

    Column {
        Button(onClick = { size = if (size == 100.dp) 200.dp else 100.dp }) {
            Text("Запустить последовательность")
        }
        Box(Modifier.size(animatedSize).background(Color.Blue))
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun GreetingPreview() {
    MyAppTheme {
        SequenceAnimationExample()
    }
}


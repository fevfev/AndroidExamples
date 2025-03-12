package com.knyazev.jetpackcomponents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knyazev.jetpackcomponents.ui.theme.JetpackComponentsTheme

/** Экраны приложения с маршрутом, заголовком и ресурсом изображения */
sealed class Screen(val route: String, val title: String, val imageRes: Int) {
    data object Home : Screen("home", "Главное меню", R.drawable.ic_launcher_foreground)
    data object ScaffoldScreen : Screen("scaffold", "Scaffold", R.drawable.img_scaffold)
    data object TopAppBarScreen : Screen("top_app_bar", "CenterAlignedTopAppBar", R.drawable.img_top_app_bar)
    data object TextScreen : Screen("text", "Text", R.drawable.img_text)
    data object ButtonScreen : Screen("button", "Button", R.drawable.img_button)
    data object OutlinedButtonScreen : Screen("outlined_button", "OutlinedButton", R.drawable.img_outlined_button)
    data object ColumnScreen : Screen("column", "Column", R.drawable.img_column)
    data object SpacerScreen : Screen("spacer", "Spacer", R.drawable.img_spacer)
    data object RowScreen : Screen("row", "Row", R.drawable.img_row)
    data object BoxScreen : Screen("box", "Box", R.drawable.img_box)
    data object LazyColumnScreen : Screen("lazy_column", "LazyColumn", R.drawable.img_lazy_column)
    data object SurfaceScreen : Screen("surface", "Surface", R.drawable.img_surface)
    data object TextFieldScreen : Screen("text_field", "TextField", R.drawable.img_text_field)
    data object DropdownMenuScreen : Screen("dropdown_menu", "DropdownMenu", R.drawable.img_dropdown_menu)
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComponentsTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.ScaffoldScreen.route) { ScaffoldScreenWithBack(navController) }
        composable(Screen.TopAppBarScreen.route) { TopAppBarScreenWithBack(navController) }
        composable(Screen.TextScreen.route) { TextScreenWithBack(navController) }
        composable(Screen.ButtonScreen.route) { ButtonScreenWithBack(navController) }
        composable(Screen.OutlinedButtonScreen.route) { OutlinedButtonScreenWithBack(navController) }
        composable(Screen.ColumnScreen.route) { ColumnScreenWithBack(navController) }
        composable(Screen.SpacerScreen.route) { SpacerScreenWithBack(navController) }
        composable(Screen.RowScreen.route) { RowScreenWithBack(navController) }
        composable(Screen.BoxScreen.route) { BoxScreenWithBack(navController) }
        composable(Screen.LazyColumnScreen.route) { LazyColumnScreenWithBack(navController) }
        composable(Screen.SurfaceScreen.route) { SurfaceScreenWithBack(navController) }
        composable(Screen.TextFieldScreen.route) { TextFieldScreenWithBack(navController) }
        composable(Screen.DropdownMenuScreen.route) { DropdownMenuScreenWithBack(navController) }
    }
}


/** Компонент с изображением и разворачивающимся описанием */
@Composable
fun ExpandableDescription(title: String, description: String, imageRes: Int) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "$title image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (expanded) "Скрыть описание" else "Подробнее",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.clickable { expanded = !expanded }
        )
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/** Универсальный шаблон экрана с верхней панелью и кнопкой "Назад" */
@Composable
fun ScreenWithBack(title: String, navController: NavController, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                content()
            }
        }
    )
}

/** Главный экран с меню перехода к демонстрационным экранам */
@Composable
fun HomeScreen(navController: NavController) {
    val screens = listOf(
        Screen.ScaffoldScreen,
        Screen.TopAppBarScreen,
        Screen.TextScreen,
        Screen.ButtonScreen,
        Screen.OutlinedButtonScreen,
        Screen.ColumnScreen,
        Screen.SpacerScreen,
        Screen.RowScreen,
        Screen.BoxScreen,
        Screen.LazyColumnScreen,
        Screen.SurfaceScreen,
        Screen.TextFieldScreen,
        Screen.DropdownMenuScreen
    )
    Scaffold(
        topBar = { TopAppBar(title = { Text("Главное меню") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(screens) { screen ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(screen.route) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(text = screen.title)
                    }
                }
            }
        }
    }
}

/** Экран для демонстрации Scaffold */
@Composable
fun ScaffoldScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Scaffold", navController = navController) {
        ExpandableDescription(
            title = "Scaffold компонент",
            description = "Scaffold задаёт базовую структуру экрана, позволяя определить верхнюю панель, основное содержимое и плавающую кнопку.",
            imageRes = Screen.ScaffoldScreen.imageRes
        )
        // Пример использования самого Scaffold внутри описания
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text("Пример Scaffold") })
            },
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Основной контент Scaffold")
                }
            }
        )
    }
}

/** Экран для демонстрации CenterAlignedTopAppBar */
@Composable
fun TopAppBarScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "CenterAlignedTopAppBar", navController = navController) {
        ExpandableDescription(
            title = "CenterAlignedTopAppBar компонент",
            description = "Верхняя панель с центровкой заголовка и опциональной навигационной иконкой.",
            imageRes = Screen.TopAppBarScreen.imageRes
        )
        CenterAlignedTopAppBar(
            title = { Text("Центрированная панель") },
            navigationIcon = {
                IconButton(onClick = { /* Доп. действие */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Пример верхней панели")
        }
    }
}

/** Экран для демонстрации Text */
@Composable
fun TextScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Text", navController = navController) {
        ExpandableDescription(
            title = "Text компонент",
            description = "Компонент Text используется для отображения текстовой информации. Его можно стилизовать через MaterialTheme.",
            imageRes = Screen.TextScreen.imageRes
        )
        Text(
            text = "Это пример текста, выводимого компонентом Text.",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

/** Экран для демонстрации Button */
@Composable
fun ButtonScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Button", navController = navController) {
        ExpandableDescription(
            title = "Button компонент",
            description = "Button – это интерактивный элемент, предназначенный для выполнения действий по нажатию.",
            imageRes = Screen.ButtonScreen.imageRes
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { /* Действие */ }) {
                Text("Нажми меня")
            }
        }
    }
}

/** Экран для демонстрации OutlinedButton */
@Composable
fun OutlinedButtonScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "OutlinedButton", navController = navController) {
        ExpandableDescription(
            title = "OutlinedButton компонент",
            description = "OutlinedButton – кнопка с обводкой, которая визуально отличается от обычной кнопки Button.",
            imageRes = Screen.OutlinedButtonScreen.imageRes
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(onClick = { /* Действие */ }) {
                Text("Outlined Button")
            }
        }
    }
}

/** Экран для демонстрации Column */
@Composable
fun ColumnScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Column", navController = navController) {
        ExpandableDescription(
            title = "Column компонент",
            description = "Column располагает дочерние элементы вертикально с заданными отступами.",
            imageRes = Screen.ColumnScreen.imageRes
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Элемент 1")
            Text("Элемент 2")
            Text("Элемент 3")
        }
    }
}

/** Экран для демонстрации Spacer */
@Composable
fun SpacerScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Spacer", navController = navController) {
        ExpandableDescription(
            title = "Spacer компонент",
            description = "Spacer используется для создания отступов между элементами.",
            imageRes = Screen.SpacerScreen.imageRes
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Сверху")
            Spacer(modifier = Modifier.height(24.dp))
            Text("Снизу")
        }
    }
}

/** Экран для демонстрации Row */
@Composable
fun RowScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Row", navController = navController) {
        ExpandableDescription(
            title = "Row компонент",
            description = "Row располагает дочерние элементы горизонтально, что полезно для создания рядов элементов.",
            imageRes = Screen.RowScreen.imageRes
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Первый")
            Text("Второй")
            Text("Третий")
        }
    }
}

/** Экран для демонстрации Box */
@Composable
fun BoxScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Box", navController = navController) {
        ExpandableDescription(
            title = "Box компонент",
            description = "Box позволяет накладывать элементы друг на друга для создания компоновок с перекрытиями.",
            imageRes = Screen.BoxScreen.imageRes
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Фон", modifier = Modifier.align(Alignment.Center))
            Button(
                onClick = { /* Действие */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("Кнопка")
            }
        }
    }
}

/** Экран для демонстрации LazyColumn */
@Composable
fun LazyColumnScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "LazyColumn", navController = navController) {
        ExpandableDescription(
            title = "LazyColumn компонент",
            description = "LazyColumn – это прокручиваемый список, который лениво подгружает данные, что удобно для длинных списков.",
            imageRes = Screen.LazyColumnScreen.imageRes
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(20) { index ->
                Text("Элемент списка #$index", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

/** Экран для демонстрации Surface */
@Composable
fun SurfaceScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "Surface", navController = navController) {
        ExpandableDescription(
            title = "Surface компонент",
            description = "Surface – это контейнер, позволяющий задать цвет фона, форму и тень для создания визуально привлекательных блоков.",
            imageRes = Screen.SurfaceScreen.imageRes
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            color = Color.LightGray,
            shape = MaterialTheme.shapes.medium,
            shadowElevation = 8.dp        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Surface: Контейнер с настройками")
            }
        }
    }
}

/** Экран для демонстрации TextField */
@Composable
fun TextFieldScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "TextField", navController = navController) {
        ExpandableDescription(
            title = "TextField компонент",
            description = "TextField – это поле для ввода текста, которое позволяет пользователю вводить и редактировать текст. Здесь можно задать подсказку, стиль и обработку ввода.",
            imageRes = Screen.TextFieldScreen.imageRes
        )
        var text by remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Введите текст") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
    }
}

/** Экран для демонстрации DropdownMenu */
@Composable
fun DropdownMenuScreenWithBack(navController: NavController) {
    ScreenWithBack(title = "DropdownMenu", navController = navController) {
        ExpandableDescription(
            title = "DropdownMenu компонент",
            description = "DropdownMenu – это выпадающее меню, которое позволяет пользователю выбрать один из нескольких вариантов.",
            imageRes = Screen.DropdownMenuScreen.imageRes
        )
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf("Выберите опцию") }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { expanded = true }) {
                Text(selectedOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Опция 1", "Опция 2", "Опция 3").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComponentsTheme {
        MyApp()
    }
}
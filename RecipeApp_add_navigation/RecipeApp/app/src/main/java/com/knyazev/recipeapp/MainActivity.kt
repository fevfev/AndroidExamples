package com.knyazev.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.ui.screens.AddRecipeScreen // Импорт из ui.screens
import com.knyazev.recipeapp.ui.screens.RecipeDetailScreen // Импорт из ui.screens
import com.knyazev.recipeapp.ui.screens.RecipeListScreen // Импорт из ui.screens
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { //setContent { ... } в Activity: Эта функция устанавливает Composable контент для Activity. Все, что вы помещаете внутрь setContent, будет отображаться на экране.
            RecipeAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {  // MaterialTheme и Surface: MaterialTheme предоставляет стилизацию Material Design для вашего приложения. Surface – это контейнер, который применяет стили темы, такие как цвет фона.
                    val navController = rememberNavController() // Создаем NavController
                    NavHost(navController = navController, startDestination = Screen.RecipeList.route) { // NavHost: Composable функция, которая является контейнером для навигации. Она связывает NavController с графом навигации (набором маршрутов). NavController: Объект, который управляет навигацией в NavHost. Мы создаем его с помощью rememberNavController().
                        composable(Screen.RecipeList.route) { // Маршрут для RecipeListScreen composable(route): Функция внутри NavHost, которая определяет маршрут и Composable функцию, которая будет отображаться при переходе на этот маршрут.
                            RecipeListScreen(navController = navController) // Передаем navController  Используем RecipeListScreen из ui.screens
                        }
                        composable(Screen.RecipeDetail.route) { // Маршрут для RecipeDetailScreen
                            RecipeDetailScreen(navController = navController) // Используем RecipeDetailScreen из ui.screens
                        }
                        composable(Screen.AddRecipe.route) { // Маршрут для AddRecipeScreen
                            AddRecipeScreen(navController = navController) // Используем AddRecipeScreen из ui.screens
                        }
                    }

                }
            }
        }
    }
}

@Composable //@Composable аннотация: Функции, помеченные @Composable, являются строительными блоками UI в Compose. Они описывают часть пользовательского интерфейса и могут быть переиспользованы и скомбинированы друг с другом.
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text( // Text: Простейший Composable для отображения текста.
        text = "Hello $name!", // содержимое, текст который мы выводим
        modifier = modifier  //Modifier: Используется для изменения внешнего вида или поведения Composable элементов. Например, modifier = Modifier.fillMaxSize() заставляет Surface занять все доступное пространство.
    )
}

@Preview(showBackground = true) // @Preview аннотация: Позволяет отображать предварительный просмотр Composable функции прямо в Android Studio, что очень удобно для разработки UI.
@Composable
fun GreetingPreview() {
    RecipeAppTheme {
        Greeting("Android")
    }
}

/*
Jetpack Compose : https://developer.android.com/jetpack/compose/documentation
Базовые композ компоненты : https://developer.android.com/jetpack/compose/tutorial
Модификаторы Compose: https://developer.android.com/jetpack/compose/modifiers
Compose Layouts разметка : https://developer.android.com/jetpack/compose/layouts (начнем использовать в следующих модулях)

Compose UI Элементы: https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary (библиотека Material Design 3 компонентов)

Navigation Compose Guide: https://developer.android.com/jetpack/compose/navigation  Библиотека Jetpack Compose для управления навигацией внутри Compose приложений. Она позволяет определять маршруты ( destinations ) и перемещаться между ними.
Navigation Compose Codelab: https://developer.android.com/codelabs/jetpack-compose-navigation


 */
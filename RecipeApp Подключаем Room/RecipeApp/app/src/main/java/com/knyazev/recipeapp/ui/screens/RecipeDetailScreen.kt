package com.knyazev.recipeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme

@Composable
fun RecipeDetailScreen(navController: NavController) { // NavController: Объект, который управляет навигацией в NavHost. Мы передаем его в RecipeDetailScreen, чтобы иметь возможность переходить на другие экраны.
    Column {
        Text(text = "Детальный экран рецепта")
        Button(onClick = { navController.navigate(Screen.RecipeList.route) }) {
            Text("К списку рецептов")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenPreview() {
    RecipeAppTheme {
        RecipeDetailScreen(navController = rememberNavController()) // Для Preview нужен NavController
    }
}
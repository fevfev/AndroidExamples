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
fun RecipeListScreen(navController: NavController) {
    Column {
        Text(text = "Список рецептов")
        Button(onClick = { navController.navigate(Screen.AddRecipe.route) }) {
            Text("Добавить рецепт")
        }
        Button(onClick = { navController.navigate(Screen.RecipeDetail.route) }) {
            Text("Детали рецепта")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeListScreenPreview() {
    RecipeAppTheme {
        RecipeListScreen(navController = rememberNavController())
    }
}
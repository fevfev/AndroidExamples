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
fun AddRecipeScreen(navController: NavController) {
    Column {
        Text(text = "Экран добавления рецепта")
        Button(onClick = { navController.navigate(Screen.RecipeList.route) }) {
            Text("К списку рецептов")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddRecipeScreenPreview() {
    RecipeAppTheme {
        AddRecipeScreen(navController = rememberNavController()) // Для Preview нужен NavController
    }
}
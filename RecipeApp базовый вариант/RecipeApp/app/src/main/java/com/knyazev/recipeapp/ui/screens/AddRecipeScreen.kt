package com.knyazev.recipeapp.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme

@Composable
fun AddRecipeScreen(navController: NavController) {
    Text(text = "Экран добавления рецепта")
}

@Preview(showBackground = true)
@Composable
fun AddRecipeScreenPreview() {
    RecipeAppTheme {
        RecipeAppTheme {
            AddRecipeScreen(navController = rememberNavController())
        }
    }
}
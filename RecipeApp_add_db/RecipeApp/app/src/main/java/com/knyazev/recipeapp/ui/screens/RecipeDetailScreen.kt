package com.knyazev.recipeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme
import com.knyazev.recipeapp.ui.viewmodel.RecipeViewModel

@Composable
fun RecipeDetailScreen(navController: NavController, recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val recipeId = backStackEntry.value?.arguments?.getLong("recipeId")

    var recipe by remember { mutableStateOf(recipeViewModel.emptyRecipe) }

    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            recipeViewModel.getRecipeById(recipeId).let { fetchedRecipe ->
                if (fetchedRecipe != null) {
                    recipe = fetchedRecipe
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = recipe.title, style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ингредиенты:", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        Text(text = recipe.ingredients)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Рецепт:", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        Text(text = recipe.instructions)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            recipeViewModel.deleteRecipe(recipe)
            navController.navigate(Screen.RecipeList.route) {
                popUpTo(Screen.RecipeList.route) { inclusive = true }
            }
        }) {
            Text("Удалить рецепт")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Назад")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeDetailScreenPreview() {
    RecipeAppTheme {
        RecipeDetailScreen(navController = rememberNavController())
    }
}
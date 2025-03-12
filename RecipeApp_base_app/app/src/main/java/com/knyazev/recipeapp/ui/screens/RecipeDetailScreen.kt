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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check

@Composable
fun RecipeDetailScreen(navController: NavController, recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)) {    val recipeId = backStackEntry.value?.arguments?.getLong("recipeId")

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
        Text(text = recipe.title, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ингредиенты:", style = MaterialTheme.typography.headlineSmall)
        // Отображение ингредиентов списком с использованием ListItem
        recipe.ingredients.forEach { ingredient ->
            ListItem( // ListItem для каждого ингредиента
                leadingContent = { Icon(Icons.Filled.Check, contentDescription = "Ингредиент") }, // Иконка чекбокса в начале строки
                headlineContent = { Text(ingredient) } // Текст ингредиента
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Рецепт:", style = MaterialTheme.typography.headlineSmall)
        // Отображение инструкций нумерованным списком
        Column { // Column для нумерованного списка инструкций
            recipe.instructions.forEachIndexed { index, instruction -> // forEachIndexed для получения индекса элемента
                Row(verticalAlignment = Alignment.Top) { // Row для номера и текста инструкции
                    Text(text = "${index + 1}. ", style = MaterialTheme.typography.bodyLarge) // Номер инструкции
                    Text(text = instruction, style = MaterialTheme.typography.bodyLarge) // Текст инструкции
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { // Кнопка "Редактировать рецепт"
            navController.navigate(Screen.EditRecipe.createRoute(recipe.id)) // Переход на EditRecipeScreen с recipe.id
        }) {
            Text("Редактировать рецепт")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {  // Кнопка "Удалить рецепт"
            recipeViewModel.deleteRecipe(recipe)
            navController.navigate(Screen.RecipeList.route) {
                popUpTo(Screen.RecipeList.route) { inclusive = true }
            }
        }) {
            Text("Удалить рецепт") // Текст кнопки
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
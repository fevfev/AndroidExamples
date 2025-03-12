package com.knyazev.recipeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.data.Recipe
import com.knyazev.recipeapp.ui.viewmodel.RecipeViewModel
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme

@OptIn(ExperimentalMaterial3Api::class) // Для TextField
@Composable
fun AddRecipeScreen(navController: NavController, recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)) {
    var title by remember { mutableStateOf("") } // State для названия рецепта
    var ingredients by remember { mutableStateOf("") } // State для ингредиентов
    var instructions by remember { mutableStateOf("") } // State для инструкций

    Column {
        Text("Добавить новый рецепт")

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название рецепта") }
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        TextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ингредиенты") }
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        TextField(
            value = instructions,
            onValueChange = { instructions = it },
            label = { Text("Инструкции") },
            minLines = 3 // Минимальное количество строк для поля инструкций
        )

        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        Button(onClick = {
            if (title.isNotBlank() && ingredients.isNotBlank() && instructions.isNotBlank()) { // Проверка на заполненность полей
                val newRecipe = Recipe(title = title, ingredients = ingredients, instructions = instructions)
                recipeViewModel.insertRecipe(newRecipe) // Сохраняем рецепт в базу данных
                navController.navigate(Screen.RecipeList.route) { // Возвращаемся к списку рецептов
                    popUpTo(Screen.RecipeList.route) { inclusive = true } // Очищаем стек навигации до списка
                }
            } else {
                // Выводим сообщение об ошибке, если поля не заполнены
            }
        }) {
            Text("Сохранить рецепт")
        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        Button(onClick = { navController.navigate(Screen.RecipeList.route) }) {
            Text("Отмена") // Кнопка "Отмена" для возврата к списку
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddRecipeScreenPreview() {
    RecipeAppTheme {
        AddRecipeScreen(navController = rememberNavController())
    }
}
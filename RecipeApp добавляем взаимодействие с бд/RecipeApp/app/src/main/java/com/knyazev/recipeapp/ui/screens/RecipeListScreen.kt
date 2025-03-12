package com.knyazev.recipeapp.ui.screens

import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.data.Recipe
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme
import com.knyazev.recipeapp.ui.viewmodel.RecipeViewModel

@Composable
fun RecipeListScreen(navController: NavController, recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)) { // Принимаем RecipeViewModel
    Scaffold( // Используем Scaffold для размещения FAB
        floatingActionButton = { // FloatingActionButton
            FloatingActionButton(onClick = { navController.navigate(Screen.AddRecipe.route) }) {
                Icon(Icons.Filled.Add, "Добавить рецепт") // Иконка "Add"
            }
        }
    ) { paddingValues -> // paddingValues для учета отступов от Scaffold
        RecipeListContent(paddingValues = paddingValues, recipeViewModel = recipeViewModel, navController = navController) // Выносим контент списка в отдельную функцию
    }
}

@Composable
fun RecipeListContent(paddingValues: PaddingValues, recipeViewModel: RecipeViewModel, navController: NavController) {
    val recipes by recipeViewModel.allRecipes.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Применяем paddingValues от Scaffold
            .padding(16.dp) // Добавляем общий отступ для списка
    ) {
        items(recipes) { recipe ->
            RecipeItem(recipe = recipe, onClick = { // Используем отдельный Composable для элемента списка
                navController.navigate(Screen.RecipeDetail.createRoute(recipe.id)) // Переход на RecipeDetailScreen с recipe.id
            })
            Spacer(modifier = Modifier.height(8.dp)) // Разделитель между элементами
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) { // Используем Card для визуального оформления элемента списка
        Column(modifier = Modifier.padding(16.dp)) { // Отступ внутри Card
            Text(text = recipe.title) // Название рецепта
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ингредиенты: ${recipe.ingredients.take(50)}...") // Краткое описание ингредиентов (обрезаем до 50 символов)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeListScreenPreview() {
    RecipeAppTheme {
        Text("Список рецептов (Preview)")
    }
}
package com.knyazev.recipeapp.ui.screens

import android.R.attr.onClick
import android.R.style
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.knyazev.recipeapp.data.Recipe
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme
import com.knyazev.recipeapp.ui.viewmodel.RecipeViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.knyazev.recipeapp.data.SortCriteria


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(navController: NavController, recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddRecipe.route) }) {
                Icon(Icons.Filled.Add, "Добавить рецепт")
            }
        }
    ) { paddingValues ->
        RecipeListContent(paddingValues = paddingValues, recipeViewModel = recipeViewModel, navController = navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListContent(paddingValues: PaddingValues, recipeViewModel: RecipeViewModel, navController: NavController) {
    val recipes by recipeViewModel.allRecipes.collectAsState()
    var searchQuery by remember { mutableStateOf("") } // State для поискового запроса - оставляем пока здесь, хотя управление в ViewModel предпочтительнее
    var expandedSortMenu by remember { mutableStateOf(false) } // State для управления выпадающим меню сортировки - ОСТАВЛЯЕМ ЗДЕСЬ, так как UI state

    Column(modifier = Modifier.padding(paddingValues)) { // Используем Column для вертикальной компоновки
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ExposedDropdownMenuBox( // Используем ExposedDropdownMenuBox для отображения выпадающего меню
                expanded = expandedSortMenu,
                onExpandedChange = { expandedSortMenu = !expandedSortMenu },
                modifier = Modifier.weight(1f)
            ) {
                TextField( // Используем TextField для отображения текущего критерия сортировки
                    readOnly = true,
                    value = when (recipeViewModel.sortCriteria.collectAsState().value) { // Используем sortCriteria из ViewModel
                        SortCriteria.TITLE -> "Сортировать по названию"
                        else -> "Сортировать"
                    },
                    onValueChange = { },
                    label = { Text("Сортировка") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSortMenu)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedSortMenu,
                    onDismissRequest = { expandedSortMenu = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DropdownMenuItem(
                        text = { Text("По названию") },
                        onClick = {
                            // selectedSortCriteria = SortCriteria.TITLE - УДАЛЯЕМ, теперь состояние в ViewModel
                            expandedSortMenu = false
                            recipeViewModel.updateSortCriteria(SortCriteria.TITLE) // Обновляем критерий в ViewModel
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Поиск рецептов") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeItem(recipe = recipe, onClick = {
                    navController.navigate(Screen.RecipeDetail.createRoute(recipe.id))
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RecipeItem(recipe: Recipe, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) { // Используем Card для визуального оформления элемента списка
        Row( // Используем Row для горизонтальной компоновки
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Выравнивание по вертикальному центру
        ) {
        Box( // Box изображение
            modifier = Modifier
                .size(60.dp) // Размер изображения
                .background(Color.LightGray) // Цвет placeholder
        )
        Spacer(modifier = Modifier.width(16.dp)) // Отступ между изображением и текстом
        Column {
        Text(text = recipe.title, // Название рецепта
            style = MaterialTheme.typography.titleMedium // Стиль для заголовка
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ингредиенты: ${recipe.ingredients.take(50)}...", // Краткое описание ингредиентов (обрезаем до 50 символов)
                style = MaterialTheme.typography.bodySmall, // Стиль для описания
                color = Color.Gray // Цвет для описания
            )
        }
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
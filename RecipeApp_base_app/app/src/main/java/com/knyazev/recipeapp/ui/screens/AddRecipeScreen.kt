package com.knyazev.recipeapp.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.knyazev.recipeapp.data.Recipe
import com.knyazev.recipeapp.ui.Screen
import com.knyazev.recipeapp.ui.theme.RecipeAppTheme
import com.knyazev.recipeapp.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(navController: NavController, recipeViewModel: RecipeViewModel = viewModel(factory = RecipeViewModel.Factory)) {
    // ... (предыдущий код AddRecipeScreen - recipeId, state variables, isError, LaunchedEffect, Column)
    var imageUri by remember { mutableStateOf<Uri?>(null) } // State для хранения URI выбранного изображения

    // ActivityResultLauncher для выбора изображения из галереи
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri // Обновляем imageUri state, когда изображение выбрано
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(if (isEditMode) "Редактировать рецепт" else "Добавить новый рецепт")

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        // UI для выбора изображения
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { galleryLauncher.launch("image/*") }) { // Row для кликабельного выбора изображения
            Box(modifier = Modifier.size(80.dp).border(1.dp, Color.LightGray)) { // Box для рамки изображения
                Image( // Composable Image для отображения выбранного изображения или placeholder
                    painter = rememberAsyncImagePainter( // Coil painter для асинхронной загрузки изображения по URI
                        model = imageUri ?: Icons.Filled.Image, // Используем imageUri, если есть, иначе placeholder иконку
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery), // Placeholder на время загрузки (можно убрать)
                        error = painterResource(id = android.R.drawable.ic_menu_gallery) // Изображение ошибки (можно убрать)
                    ),
                    contentDescription = "Изображение рецепта",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Обрезаем изображение, чтобы соответствовало размеру Box
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { galleryLauncher.launch("image/*") }) { // Кнопка "Выбрать изображение"
                Text("Выбрать изображение")
            }
        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        TextField( // TextField для названия рецепта - без изменений
            value = title,
            onValueChange = { title = it },
            label = { Text("Название рецепта") },
            isError = isError && title.isBlank()
        )
        if (isError && title.isBlank()) {
            Text(text = "Название рецепта не может быть пустым", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        TextField( // TextField для ингредиентов - без изменений
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ингредиенты (каждый ингредиент на новой строке)") },
            placeholder = { Text("Например:\n- 200г спагетти\n- 2 яйца\n- 50г бекона") },
            minLines = 4,
            isError = isError && ingredients.isBlank()
        )
        if (isError && ingredients.isBlank()) {
            Text(text = "Ингредиенты не могут быть пустыми", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        TextField( // TextField для инструкций - без изменений
            value = instructions,
            onValueChange = { instructions = it },
            label = { Text("Инструкции (каждый шаг на новой строке)") },
            placeholder = { Text("Например:\n1. Отварите спагетти...\n2. Обжарьте бекон...") },
            minLines = 5,
            isError = isError && instructions.isBlank()
        )
        if (isError && instructions.isBlank()) {
            Text(text = "Инструкции не могут быть пустыми", color = MaterialTheme.colorScheme.error)
        }


        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        Button(onClick = { // Кнопка "Сохранить рецепт" - обновление сохранения imageUri
            if (title.isNotBlank() && ingredients.isNotBlank() && instructions.isNotBlank()) {
                isError = false
                val ingredientsList = ingredients.split("\n").filter { it.isNotBlank() }.toList()
                val instructionsList = instructions.split("\n").filter { it.isNotBlank() }.toList()
                val recipeToSave = Recipe(title = title, ingredients = ingredientsList, instructions = instructionsList, imageUri = imageUri.toString()) // Сохраняем imageUri
                if (isEditMode && recipeId != null) {
                    recipeViewModel.updateRecipe(recipeToSave.copy(id = recipeId))
                } else {
                    recipeViewModel.insertRecipe(recipeToSave)
                }
                navController.navigate(Screen.RecipeList.route) {
                    popUpTo(Screen.RecipeList.route) { inclusive = true }
                }
            } else {
                isError = true
            }
        }) {
            Text(if (isEditMode) "Сохранить изменения" else "Сохранить рецепт")
        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

        Button(onClick = { navController.navigate(Screen.RecipeList.route) }) {
            Text("Отмена")
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
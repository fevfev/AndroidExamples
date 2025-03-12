package com.knyazev.recipeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.knyazev.recipeapp.data.Recipe
import com.knyazev.recipeapp.data.RecipeDao
import com.knyazev.recipeapp.data.RecipeDatabase

class RecipeViewModel(private val recipeDao: RecipeDao) : ViewModel() {

    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()
    val emptyRecipe = Recipe(id = 0, title = "", ingredients = "", instructions = "") // Пустой рецепт для инициализации state

    suspend fun getRecipeById(id: Long): Recipe? { // Функция для получения рецепта по ID
        return recipeDao.getRecipeById(id)
    }
    init {
        insertInitialRecipes() // Добавляем тестовые рецепты при инициализации ViewModel
    }

    private fun insertInitialRecipes() {
        viewModelScope.launch { // Запускаем корутину в viewModelScope
            recipeDao.getAllRecipes().collect { recipes ->
                if (recipes.isEmpty()) { // Проверяем, пуста ли база данных
                    val initialRecipes = listOf(
                        Recipe(title = "Паста Карбонара", ingredients = "Спагетти, Яйца, Бекон, Пармезан, Перец", instructions = "Отварить пасту. Обжарить бекон. Смешать яйца с пармезаном и перцем. Соединить все ингредиенты."),
                        Recipe(title = "Омлет", ingredients = "Яйца, Молоко, Сыр, Зелень, Соль, Перец", instructions = "Взбить яйца с молоком, солью и перцем. Добавить сыр и зелень. Жарить на сковороде до готовности.")
                    )
                    initialRecipes.forEach { recipeDao.insertRecipe(it) } // Вставляем рецепты в базу данных
                }
            }
        }
    }

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.insertRecipe(recipe)
        }
    }




    fun deleteRecipe(recipe: Recipe) { // Функция для удаления рецепта
        viewModelScope.launch {
            recipeDao.deleteRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.updateRecipe(recipe)
        }
    }

    // Factory для создания ViewModel с параметром (RecipeDao)
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application context from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // Get RecipeDao from Database
                val database = RecipeDatabase.getDatabase(application)
                val recipeDao = database.recipeDao()
                // Create RecipeViewModel with RecipeDao
                return RecipeViewModel(recipeDao) as T
            }
        }
    }
}
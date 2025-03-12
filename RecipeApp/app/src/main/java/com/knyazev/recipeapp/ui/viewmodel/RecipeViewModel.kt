package com.knyazev.recipeapp.ui.viewmodel

import android.text.TextUtils.isEmpty
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import com.knyazev.recipeapp.data.Recipe
import com.knyazev.recipeapp.data.RecipeDao
import com.knyazev.recipeapp.data.RecipeDatabase
import com.knyazev.recipeapp.data.SortCriteria

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn



class RecipeViewModel(private val recipeDao: RecipeDao) : ViewModel() {// ViewModel для работы с рецептами

    private val _searchQuery = MutableStateFlow("") // StateFlow для поискового запроса
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow() // Преобразуем в StateFlow

    private val _sortCriteria = MutableStateFlow(SortCriteria.TITLE) // StateFlow для критерия сортировки, по умолчанию TITLE
    val sortCriteria: StateFlow<SortCriteria> = _sortCriteria.asStateFlow() // Преобразуем в StateFlow

    private val _allRecipesFlow: Flow<List<Recipe>> = recipeDao.getAllRecipes() // Flow для получения всех рецептов из базы данных

    val allRecipes: StateFlow<List<Recipe>> = combine(searchQuery, sortCriteria, _allRecipesFlow) { query, criteria, recipes -> // Добавляем sortCriteria в combine
        val filteredRecipes = if (query.isBlank()) { // Фильтруем рецепты по поисковому запросу
            recipes
        } else {
            recipes.filter { recipe -> // Фильтруем по названию и ингредиентам
                recipe.title.contains(query, ignoreCase = true) ||
                        recipe.ingredients.contains(query,)
            }
        }

        when (criteria) { // Применяем сортировку в зависимости от выбранного критерия
            SortCriteria.TITLE -> filteredRecipes.sortedBy { it.title } // Сортировка по названию
            // Можно добавить другие case для других критериев сортировки
            else -> filteredRecipes // По умолчанию не сортируем (или можно вернуть сортировку по какому-то критерию по умолчанию)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        insertInitialRecipes()
    }


    private fun insertInitialRecipes() {
        viewModelScope.launch {
            if (recipeDao.getAllRecipes().isEmpty()) {
                val initialRecipes = listOf(
                    Recipe(
                        title = "Паста Карбонара",
                        ingredients = listOf("Спагетти", "Яйца", "Бекон", "Пармезан", "Черный перец"), // Ингредиенты как список
                        instructions = listOf( // Инструкции как список
                            "Отварите спагетти в подсоленной воде до состояния аль денте.",
                            "Пока варится паста, нарежьте бекон кубиками и обжарьте на сковороде до хрустящего состояния.",
                            "В миске взбейте яйца с тертым пармезаном и свежемолотым черным перцем.",
                            "Слейте воду с пасты, оставив немного крахмальной воды.",
                            "Добавьте пасту к бекону, перемешайте.",
                            "Снимите сковороду с огня и быстро влейте яичную смесь, постоянно помешивая, чтобы яйца не свернулись.",
                            "При необходимости добавьте немного крахмальной воды для создания кремовой консистенции.",
                            "Подавайте немедленно, посыпав дополнительным пармезаном и черным перцем."
                        )
                    ),
                    Recipe(
                        title = "Омлет",
                        ingredients = listOf("Яйца", "Молоко", "Сыр", "Зелень (петрушка, укроп)", "Соль", "Черный перец"), // Ингредиенты как список
                        instructions = listOf( // Инструкции как список
                            "В миске взбейте яйца с молоком, солью и черным перцем.",
                            "Добавьте тертый сыр и мелко нарезанную зелень.",
                            "Разогрейте сковороду с небольшим количеством масла.",
                            "Вылейте яичную смесь на сковороду.",
                            "Жарьте на среднем огне, пока омлет не схватится снизу и сверху.",
                            "Подавайте горячим, по желанию можно добавить начинку (овощи, грибы, ветчину)."
                        )
                    )
                )
                initialRecipes.forEach { recipeDao.insertRecipe(it) }
            }
        }
    }
    val emptyRecipe = Recipe(id = 0, title = "", ingredients = emptyList(), instructions = emptyList(), imageUri = null)

    fun updateSearchQuery(query: String) { // Функция для обновления поискового запроса
        _searchQuery.value = query
    }

    fun updateSortCriteria(criteria: SortCriteria) { // Функция для обновления критерия сортировки
        _sortCriteria.value = criteria
    }

    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipeDao.insertRecipe(recipe)
        }

        suspend fun getRecipeById(id: Long): Recipe? { // Функция для получения рецепта по ID
            return recipeDao.getRecipeById(id)
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
                return RecipeViewModel(recipeDao) as T
            }
        }
    }
}
package com.knyazev.recipeapp.ui

sealed class Screen(val route: String) {
    object RecipeList : Screen("recipe_list")
    object AddRecipe : Screen("add_recipe")
    object RecipeDetail : Screen("recipe_detail/{recipeId}") { // Маршрут для RecipeDetailScreen с параметром recipeId
        fun createRoute(recipeId: Long): String { // Функция для создания маршрута с конкретным recipeId
            return "recipe_detail/$recipeId"
        }
    }
}
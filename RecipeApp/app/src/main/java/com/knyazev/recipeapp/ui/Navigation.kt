package com.knyazev.recipeapp.ui

sealed class Screen(val route: String) {
    object RecipeList : Screen("recipe_list")
    object AddRecipe : Screen("add_recipe")
    object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Long): String {
            return "recipe_detail/$recipeId"
        }
    }
    object EditRecipe : Screen("edit_recipe/{recipeId}") { // Маршрут для EditRecipeScreen с параметром recipeId
        fun createRoute(recipeId: Long): String {
            return "edit_recipe/$recipeId"
        }
    }
}
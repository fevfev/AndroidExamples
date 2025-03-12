package com.knyazev.recipeapp.ui

sealed class Screen(val route: String) { //sealed class позволяет нам перечислить все возможные экраны (маршруты) в приложении.
    object RecipeList : Screen("recipe_list")
    object RecipeDetail : Screen("recipe_detail")
    object AddRecipe : Screen("add_recipe")
}


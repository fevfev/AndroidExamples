package com.knyazev.recipeapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao // Аннотация Room, указывающая, что это DAO @Dao: Аннотация Room, указывающая, что это интерфейс Data Access Object (DAO). DAO определяет методы для доступа к базе данных.
interface RecipeDao {
    @Insert // Аннотация для операции вставки    @Insert, @Update, @Delete: Аннотации Room для стандартных операций CRUD. suspend fun означает, что эти функции будут выполняться асинхронно с использованием корутин.
    suspend fun insertRecipe(recipe: Recipe)

    @Update // Аннотация для операции обновления
    suspend fun updateRecipe(recipe: Recipe)

    @Delete // Аннотация для операции удаления
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes") // Аннотация для SQL-запроса  @Query("SELECT ..."): Аннотация для выполнения произвольных SQL-запросов.
    fun getAllRecipes(): Flow<List<Recipe>> // Flow для асинхронного получения списка рецептов  Возвращает Flow<List<Recipe>>. Flow из Kotlin Coroutines используется для асинхронного потока данных

    @Query("SELECT * FROM recipes WHERE id = :id") // Запрос с параметром :id для получения рецепта по его ID
    suspend fun getRecipeById(id: Long): Recipe? // Получение рецепта по ID
}
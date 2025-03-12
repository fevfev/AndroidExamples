package com.knyazev.recipeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes") // Указываем имя таблицы в базе данных @Entity(tableName = "recipes"): Аннотация Room, указывающая, что этот класс является Entity (сущностью) для таблицы в базе данных. tableName задает имя таблицы.
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // PrimaryKey, автогенерация ID  @PrimaryKey(autoGenerate = true) val id: Long = 0: Аннотация для первичного ключа. autoGenerate = true означает, что Room будет автоматически генерировать ID при добавлении новых рецептов. Long = 0 задает значение по умолчанию, которое будет перезаписано при вставке в базу.
    val title: String,
    val ingredients: String, // Пока храним ингредиенты в виде строки, потом можно сделать список
    val instructions: String // Пока шаги приготовления тоже строкой
)

/*
Room Persistence Library Guide: https://developer.android.com/training/data-storage/room

Room with Kotlin Coroutines and Flow: https://developer.android.com/kotlin/coroutines/coroutines-adv-flow

Database Migrations in Room: https://developer.android.com/training/data-storage/room/migrating-db-versions
* */
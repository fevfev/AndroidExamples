package com.knyazev.recipeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val imageUri: String? = null // Добавляем поле imageUri типа String?, может быть null, если изображение не выбрано
)

// TypeConverters для преобразования List<String> в String и обратно для Room
class Converters {
    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return Gson().toJson(list)
    }
}
/*
Room Persistence Library Guide: https://developer.android.com/training/data-storage/room

Room with Kotlin Coroutines and Flow: https://developer.android.com/kotlin/coroutines/coroutines-adv-flow

Database Migrations in Room: https://developer.android.com/training/data-storage/room/migrating-db-versions
* */
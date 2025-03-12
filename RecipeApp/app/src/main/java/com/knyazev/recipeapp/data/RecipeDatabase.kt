package com.knyazev.recipeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Добавляем TypeConverters аннотацию
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao // Абстрактная функция для получения DAO (Data Access Object)

    companion object { // Singleton pattern для Database
        @Volatile
        private var Instance: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            // if Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RecipeDatabase::class.java, "recipe_database")
                    .fallbackToDestructiveMigration(false) // Стратегия миграции (для простых случаев)
                    .build()
                    .also { Instance = it } // Присваиваем Instance и возвращаем
            }
        }
    }
}


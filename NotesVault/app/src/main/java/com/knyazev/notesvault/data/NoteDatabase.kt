package com.knyazev.notesvault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1, exportSchema = false) // Определение сущностей и версии БД
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao // Абстрактный метод для получения DAO

    companion object {
        @Volatile // Переменная видима всем потокам
        private var INSTANCE: NoteDatabase? = null // Ссылка на экземпляр базы данных

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) { // Синхронизированный блок для создания единственного экземпляра
                val instance = Room.databaseBuilder( // Создание базы данных
                    context.applicationContext, // Контекст приложения
                    NoteDatabase::class.java, // Класс базы данных
                    "note_database" // Имя файла базы данных
                )
                    .fallbackToDestructiveMigration() // При изменении схемы - пересоздание БД (для простоты в примере)
                    .build() // Создание базы данных
                INSTANCE = instance // Сохранение ссылки на экземпляр
                return instance // Возврат экземпляра
            }
        }
    }
}
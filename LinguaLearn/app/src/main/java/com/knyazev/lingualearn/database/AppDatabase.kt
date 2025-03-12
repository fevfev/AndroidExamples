package com.knyazev.lingualearn.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.knyazev.lingualearn.model.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Word::class], version = 4, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN imageUrl TEXT")
                db.execSQL("ALTER TABLE words ADD COLUMN audioUrl TEXT")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN nextReviewTimestamp INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
                db.execSQL("ALTER TABLE words ADD COLUMN repetitionLevel INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN difficulty REAL NOT NULL DEFAULT 2.5")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lingua_learn_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Pre-populate data (example)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.wordDao().insertWord(Word(word = "hello", translation = "привет", language = "en", audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", exampleSentence = "Hello, how are you?"))
                                    database.wordDao().insertWord(Word(word = "world", translation = "мир", language = "en", imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/World_map_blank_without_borders.svg/1920px-World_map_blank_without_borders.svg.png", exampleSentence = "The world is a big place."))
                                    database.wordDao().insertWord(Word(word = "hola", translation = "привет", language = "es", exampleSentence = "Hola, ¿cómo estás?"))
                                    database.wordDao().insertWord(Word(word = "mundo", translation = "мир", language = "es", exampleSentence = "El mundo es un lugar grande."))
                                    database.wordDao().insertWord(Word(word = "bonjour", translation = "привет", language = "fr", exampleSentence = "Bonjour, comment allez-vous?"))
                                    database.wordDao().insertWord(Word(word = "monde", translation = "мир", language = "fr", exampleSentence = "Le monde est un grand endroit."))
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
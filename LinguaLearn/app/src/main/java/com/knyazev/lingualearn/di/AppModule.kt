package com.knyazev.lingualearn.di

import android.content.Context
import com.knyazev.lingualearn.database.AppDatabase
import com.knyazev.lingualearn.database.WordDao
import com.knyazev.lingualearn.network.LinguaApi
import com.knyazev.lingualearn.notification.NotificationHelper
import com.knyazev.lingualearn.repository.QuizRepository
import com.knyazev.lingualearn.repository.WordRepository
import com.knyazev.lingualearn.sound.SoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideWordDao(db: AppDatabase): WordDao = db.wordDao()

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }
    @Provides
    @Singleton
    fun provideSoundManager(@ApplicationContext context: Context): SoundManager {
        return SoundManager(context)  }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }
    @Provides
    @Singleton
    fun provideQuizRepository(apiService: LinguaApi): QuizRepository {
        return QuizRepository(apiService.retrofitService)
    }
}
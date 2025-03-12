package com.knyazev.lingualearn.database

import androidx.room.*
import com.knyazev.lingualearn.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE language = :language")
    fun getWordsByLanguage(language: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :wordId")
    fun getWordById(wordId: Long): Flow<Word?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: Word)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM words WHERE learned = 1 AND language = :language")
    fun getLearnedWords(language: String): Flow<List<Word>>

    @Query("UPDATE words SET learned = :isLearned WHERE id = :wordId")
    suspend fun updateLearnedStatus(wordId: Long, isLearned: Boolean)

    @Query("SELECT * FROM words WHERE language = :language AND nextReviewTimestamp <= :currentTime ORDER BY nextReviewTimestamp ASC")
    fun getWordsToReview(language: String, currentTime: Long): Flow<List<Word>>

    @Query("UPDATE words SET repetitionLevel = :repetitionLevel, nextReviewTimestamp = :nextReviewTimestamp, difficulty = :difficulty, learned = :learned WHERE id = :wordId")
    suspend fun updateRepetitionData(wordId: Long, repetitionLevel: Int, nextReviewTimestamp: Long, difficulty: Float, learned: Boolean)

    @Query("UPDATE words SET learned = 0, repetitionLevel = 0, nextReviewTimestamp = :currentTime, difficulty = 2.5 WHERE id = :wordId")
    suspend fun resetWordProgress(wordId: Long, currentTime: Long)
}
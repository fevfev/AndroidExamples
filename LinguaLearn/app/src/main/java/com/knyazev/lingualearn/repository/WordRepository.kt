package com.knyazev.lingualearn.repository

import android.util.Log
import com.knyazev.lingualearn.database.WordDao
import com.knyazev.lingualearn.model.DifficultyLevel
import com.knyazev.lingualearn.model.Word
import com.knyazev.lingualearn.network.LinguaApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WordRepository @Inject constructor(private val wordDao: WordDao) {

    fun getWordsByLanguage(language: String): Flow<List<Word>> = wordDao.getWordsByLanguage(language)

    fun getWordById(wordId: Long): Flow<Word?> = wordDao.getWordById(wordId)

    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun updateWord(word: Word) = wordDao.updateWord(word)

    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)

    fun getLearnedWords(language: String): Flow<List<Word>> = wordDao.getLearnedWords(language)

    suspend fun updateLearnedStatus(wordId: Long, isLearned: Boolean) = wordDao.updateLearnedStatus(wordId, isLearned)

    fun getWordsToReview(language: String, difficultyLevel: DifficultyLevel): Flow<List<Word>>{
        val currentTime = System.currentTimeMillis()
        return when(difficultyLevel){
            DifficultyLevel.EASY -> wordDao.getWordsToReview(language, currentTime)
            DifficultyLevel.MEDIUM -> wordDao.getWordsToReview(language, currentTime).map { words ->
                words.filter { !it.learned }
            }
            DifficultyLevel.HARD -> wordDao.getWordsToReview(language, currentTime).map { words ->
                words.filter { it.repetitionLevel > 2 && !it.learned }
            }
        }
    }

    suspend fun updateRepetitionLevel(wordId: Long, correct: Boolean) {
        val word = wordDao.getWordById(wordId).first() ?: return
        val newRepetitionLevel: Int
        val newDifficulty: Float

        if (correct) {
            newRepetitionLevel = word.repetitionLevel + 1
            newDifficulty = (word.difficulty + (0.1f * (5 - 3))).coerceIn(1.3f, 2.5f) // E.g., SuperMemo-2 algorithm
        } else {
            newRepetitionLevel = 0
            newDifficulty = (word.difficulty - 0.2f).coerceIn(1.3f, 2.5f)
        }
        val interval = calculateInterval(newRepetitionLevel, newDifficulty)
        val nextReviewTimestamp = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(interval.toLong())

        wordDao.updateRepetitionData(wordId, newRepetitionLevel, nextReviewTimestamp, newDifficulty, newRepetitionLevel >=4)
    }

    private fun calculateInterval(repetitionLevel: Int, difficulty: Float): Int {
        return when (repetitionLevel) {
            0 -> 1
            1 -> 6
            else -> (calculateInterval(repetitionLevel - 1, difficulty) * difficulty).toInt()
        }
    }

    suspend fun resetWordProgress(wordId: Long) {
        wordDao.resetWordProgress(wordId, System.currentTimeMillis())
    }
    suspend fun refreshWords(language: String) {
        try{
            val words = LinguaApi.retrofitService.getWords().filter { it.language == language }
            words.forEach { wordDao.insertWord(it) }
        }
        catch (e: Exception){
            // TODO: Handle error
            Log.e("WordRepository", "Error fetching words: ${e.message}")
        }
    }
}
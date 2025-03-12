package com.knyazev.lingualearn.repository

import com.knyazev.lingualearn.model.Quiz
import com.knyazev.lingualearn.network.LinguaApi
import javax.inject.Inject

class QuizRepository @Inject constructor(private val apiService: LinguaApi.retrofitService) {
    suspend fun getQuizzes(language: String): List<Quiz> {
        return try {
            apiService.getQuizzes().filter { it.language == language }
        } catch (e: Exception) {
            // TODO: Handle error
            emptyList()
        }
    }
}
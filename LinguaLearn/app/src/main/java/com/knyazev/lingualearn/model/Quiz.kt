package com.knyazev.lingualearn.model

data class Quiz(
    val id: String,
    val name: String,
    val questions: List<Question>,
    val language: String
)

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val wordId: Long? = null
)
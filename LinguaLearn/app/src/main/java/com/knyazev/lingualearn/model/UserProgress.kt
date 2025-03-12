package com.knyazev.lingualearn.model

data class UserProgress(
    val userId: String,
    val currentLessonId: String,
    val learnedWords: List<Long>,
    val points: Int,
    val level: Int
)
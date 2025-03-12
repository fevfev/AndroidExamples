package com.knyazev.lingualearn.model

data class Lesson(
    val id: String,
    val name: String,
    val words: List<Word>
)
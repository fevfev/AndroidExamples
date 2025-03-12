package com.knyazev.lingualearn.model

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val goal: Int,
    var isUnlocked: Boolean = false
)
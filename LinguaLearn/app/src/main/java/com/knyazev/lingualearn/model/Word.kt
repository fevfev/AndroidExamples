package com.knyazev.lingualearn.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translation: String,
    val language: String,
    val exampleSentence: String? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    var learned: Boolean = false,
    var nextReviewTimestamp: Long = System.currentTimeMillis(),
    var repetitionLevel: Int = 0,
    var difficulty: Float = 2.5f
) {
    fun updateNextReviewTimestamp() {
        val daysToAdd = when (repetitionLevel) {
            0 -> 1
            1 -> 6
            else -> (calculateInterval(repetitionLevel - 1, difficulty) * difficulty).toInt()
        }
        nextReviewTimestamp = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(daysToAdd.toLong())
    }

    private fun calculateInterval(repetitionLevel: Int, difficulty: Float): Int {
        return when (repetitionLevel) {
            0 -> 1
            1 -> 6
            else -> (calculateInterval(repetitionLevel - 1, difficulty) * difficulty).toInt()
        }
    }
}
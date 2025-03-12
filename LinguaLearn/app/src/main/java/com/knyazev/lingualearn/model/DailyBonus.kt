package com.knyazev.lingualearn.model

import java.util.concurrent.TimeUnit

data class DailyBonus(
    val amount: Long,
    val lastClaimedTimestamp: Long = 0L
) {
    fun canClaim(): Boolean {
        val now = System.currentTimeMillis()
        val diffInMillis = now - lastClaimedTimestamp
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        return diffInDays >= 1
    }
}
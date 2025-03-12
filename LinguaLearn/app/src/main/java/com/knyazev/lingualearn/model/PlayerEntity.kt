package com.knyazev.lingualearn.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val id: Int = 0,
    var coins: Long,
    var incomePerTap: Long,
    var currentLevel: Int,
    @Embedded var prestige: Prestige = Prestige()
)
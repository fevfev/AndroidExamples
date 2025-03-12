package com.knyazev.lingualearn.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upgrades")
data class UpgradeEntity(
    @PrimaryKey val id: String,
    val playerId: Int = 0,
    var level: Int
)
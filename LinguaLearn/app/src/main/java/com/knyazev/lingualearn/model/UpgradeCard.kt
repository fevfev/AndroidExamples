package com.knyazev.lingualearn.model

enum class UpgradeType {
    CLICK_UPGRADE,
    PASSIVE_INCOME
}

data class UpgradeCard(
    val id: String,
    val name: String,
    val description: String,
    val cost: Long,
    val incomePerTap: Long,
    val incomePerSecond: Long,
    var level: Int = 0,
    val type: UpgradeType
)
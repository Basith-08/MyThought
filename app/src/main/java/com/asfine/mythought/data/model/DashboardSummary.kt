package com.asfine.mythought.data.model

data class DashboardSummary(
    val totalThoughts: Int,
    val todayThoughts: Int,
    val activeDays: Int,
    val totalWords: Int,
    val weeklyActivity: List<WeeklyActivity>,
    val recentThoughts: List<Thought>
)
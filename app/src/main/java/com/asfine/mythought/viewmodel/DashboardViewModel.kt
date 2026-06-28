package com.asfine.mythought.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asfine.mythought.data.model.DashboardSummary
import com.asfine.mythought.data.model.WeeklyActivity
import com.asfine.mythought.data.repository.ThoughtRepository
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = ThoughtRepository()
    var summary by mutableStateOf<DashboardSummary?>(null)
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadDashboard() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                errorMessage = null
                summary = repository.getDashboardSummary()
            } catch (e: Exception) {
                errorMessage = e.message
                summary = emptyDashboardSummary()
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun emptyDashboardSummary(): DashboardSummary {
        val locale = Locale.forLanguageTag("id-ID")
        val weeklyActivity = (6 downTo 0).map { offset ->
            val date = LocalDate.now().minusDays(offset.toLong())
            WeeklyActivity(
                day = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                total = 0
            )
        }
        return DashboardSummary(
            totalThoughts = 0,
            todayThoughts = 0,
            activeDays = 0,
            totalWords = 0,
            weeklyActivity = weeklyActivity,
            recentThoughts = emptyList()
        )
    }
}
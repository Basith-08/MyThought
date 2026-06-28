package com.asfine.mythought.data.repository

import com.asfine.mythought.core.toJakartaDateTimeOrNull
import com.asfine.mythought.data.SupabaseProvider
import com.asfine.mythought.data.model.DashboardSummary
import com.asfine.mythought.data.model.Thought
import com.asfine.mythought.data.model.WeeklyActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class ThoughtRepository {
    private val client = SupabaseProvider.client

    suspend fun saveThought(content: String) {
        val userId = client.auth.currentSessionOrNull()?.user?.id
            ?: error("Sesi login tidak ditemukan. Silakan login ulang.")

        client
            .from("thoughts")
            .insert(
                mapOf(
                    "content" to content,
                    "user_id" to userId
                )
            )
    }
    suspend fun getThoughts(): List<Thought> {
        return client
            .from("thoughts")
            .select {
                order(
                    column = "created_at",
                    order = Order.DESCENDING
                )
            }
            .decodeList()
    }
    suspend fun getThoughtById(id: String): Thought {
        return client
            .from("thoughts")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle()
    }

    suspend fun updateThought(
        id: String,
        content: String
    ) {

        client
            .from("thoughts")
            .update(
                {
                    set("content", content)
                }
            ) {
                filter {
                    eq("id", id)
                }
            }
    }
    suspend fun deleteThought(
        id: String
    ) {
        client
            .from("thoughts")
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
    suspend fun getDashboardSummary(): DashboardSummary {
        val thoughts = getThoughts()
        val today = LocalDate.now()
        val thoughtDates = thoughts.mapNotNull {
            it.createdAt.toJakartaDateTimeOrNull()
        }
        val totalThoughts = thoughts.size
        val todayThoughts = thoughtDates.count {
            it.toLocalDate() == today
        }
        val activeDays = thoughtDates
            .map { it.toLocalDate() }
            .distinct()
            .size
        val totalWords = thoughts.sumOf {
            it.content
                .trim()
                .split("\\s+".toRegex())
                .size
        }
        val weekly = mutableListOf<WeeklyActivity>()
        repeat(7) { i ->
            val date = today.minusDays((6 - i).toLong())
            val count = thoughtDates.count {
                it.toLocalDate() == date
            }
            weekly.add(
                WeeklyActivity(
                    day = date.dayOfWeek
                        .getDisplayName(
                            TextStyle.SHORT,
                            Locale.forLanguageTag("id-ID")
                        ),
                    total = count
                )
            )
        }
        return DashboardSummary(
            totalThoughts = totalThoughts,
            todayThoughts = todayThoughts,
            activeDays = activeDays,
            totalWords = totalWords,
            weeklyActivity = weekly,
            recentThoughts = thoughts.take(3)
        )
    }
}

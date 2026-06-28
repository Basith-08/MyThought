package com.asfine.mythought.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asfine.mythought.data.model.WeeklyActivity
import kotlin.math.max

@Composable
fun WeeklyChartCard(
    weeklyActivity: List<WeeklyActivity>
) {
    val maxValue = max(
        weeklyActivity.maxOfOrNull { it.total } ?: 1,
        1
    )
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Aktivitas 7 Hari",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyActivity.forEach {
                    val height =
                        if (it.total == 0)
                            12
                        else
                            (it.total * 100 / maxValue).coerceAtLeast(12)
                    DayBar(
                        day = it.day,
                        height = height,
                        value = it.total
                    )
                }
            }
        }
    }
}

@Composable
private fun DayBar(
    day: String,
    height: Int,
    value: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(22.dp)
                .height(height.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(8.dp)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
package com.asfine.mythought.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TextSnippet
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asfine.mythought.data.model.DashboardSummary
import com.asfine.mythought.data.model.Thought
import com.asfine.mythought.ui.components.AppTopBar
import com.asfine.mythought.ui.dashboard.components.DashboardCard
import com.asfine.mythought.ui.dashboard.components.QuickActionCard
import com.asfine.mythought.ui.dashboard.components.RecentThoughtCard
import com.asfine.mythought.ui.dashboard.components.WeeklyChartCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onWrite: () -> Unit,
    onHistory: () -> Unit,
    onThoughtClick: (Thought) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard",
                subtitle = "Selamat datang kembali 👋"
            )
        }
    ) { padding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    Row {

                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Catatan",
                            value = summary.totalThoughts.toString(),
                            icon = Icons.Outlined.Description
                        )

                        Spacer(Modifier.width(12.dp))

                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Hari Ini",
                            value = summary.todayThoughts.toString(),
                            icon = Icons.Outlined.Today
                        )

                    }
                }
                item {

                    Row {

                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Hari Aktif",
                            value = summary.activeDays.toString(),
                            icon = Icons.Outlined.LocalFireDepartment
                        )

                        Spacer(Modifier.width(12.dp))

                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Kata",
                            value = summary.totalWords.toString(),
                            icon = Icons.AutoMirrored.Outlined.TextSnippet
                        )

                    }

                }
                item {

                    WeeklyChartCard(
                        weeklyActivity = summary.weeklyActivity
                    )

                }
                item {

                    Text(
                        text = "Quick Action",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Row {

                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Tulis",
                            icon = Icons.Outlined.EditNote,
                            onClick = onWrite
                        )

                        Spacer(Modifier.width(12.dp))

                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            title = "Riwayat",
                            icon = Icons.Outlined.History,
                            onClick = onHistory
                        )

                    }

                }
                if (summary.recentThoughts.isEmpty()) {
                    item {
                        Text("Belum ada catatan.")
                    }
                } else {
                    items(summary.recentThoughts) { thought ->
                        RecentThoughtCard(
                            thought = thought,
                            onClick = {
                                onThoughtClick(thought)
                            }
                        )
                    }
                }
            }
        }
    }
}
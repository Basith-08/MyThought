package com.asfine.mythought.ui.history

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asfine.mythought.core.toJakartaDateTimeOrNull
import com.asfine.mythought.data.model.Thought
import com.asfine.mythought.ui.components.AppTopBar
import com.asfine.mythought.ui.components.EmptyState
import com.asfine.mythought.ui.components.ExpandableDateHeader
import com.asfine.mythought.ui.components.TimelineItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    thoughts: List<Thought>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onThoughtClick: (Thought) -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    var expandedHeader by remember {
        mutableStateOf<String?>(null)
    }
    val groupedThoughts = thoughts
        .sortedByDescending {
            it.createdAt.toJakartaDateTimeOrNull()?.toInstant()
        }
        .groupBy { thought ->
            val date = thought.createdAt.toJakartaDateTimeOrNull()?.toLocalDate()
            if (date == null) {
                "Tanggal tidak valid"
            } else {
                when (date) {
                    LocalDate.now() ->
                        "Hari Ini"
                    LocalDate.now().minusDays(1) ->
                        "Kemarin"
                    else -> date.format(
                        DateTimeFormatter.ofPattern(
                            "dd MMMM yyyy",
                            Locale.forLanguageTag("id-ID")
                        )
                    )
                }
            }
        }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Riwayat",
                subtitle = "Semua pikiran yang pernah kamu simpan."
            )
        }
    ) { padding ->
        PullToRefreshBox(
            state = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) {
            if (thoughts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {

                    EmptyState()
                }

            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    groupedThoughts.forEach { (header, items) ->
                        item {
                            ExpandableDateHeader(
                                title = header,
                                count = items.size,
                                expanded = expandedHeader == header,
                                onClick = {
                                    expandedHeader =
                                        if (expandedHeader == header)
                                            null
                                        else
                                            header
                                }
                            )
                        }
                        if (expandedHeader == header) {
                            items(items) { thought ->
                                TimelineItem(
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
    }
}
package com.asfine.mythought.ui.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.asfine.mythought.data.model.Thought
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun RecentThoughtCard(
    thought: Thought,
    onClick: () -> Unit
) {
    val time = try {
        OffsetDateTime
            .parse(thought.createdAt)
            .atZoneSameInstant(
                ZoneId.of("Asia/Jakarta")
            )
            .format(
                DateTimeFormatter.ofPattern(
                    "HH:mm",
                    Locale.forLanguageTag("id-ID")
                )
            )
    } catch ( _ : Exception) {
        "--:--"
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null
                )
                Spacer(
                    Modifier.width(8.dp)
                )
                Text(
                    text = time,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(
                Modifier.height(12.dp)
            )
            HorizontalDivider()
            Spacer(
                Modifier.height(12.dp)
            )
            Text(
                text = thought.content,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
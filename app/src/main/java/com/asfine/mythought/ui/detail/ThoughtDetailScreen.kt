package com.asfine.mythought.ui.detail

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.asfine.mythought.core.toJakartaDateTimeOrNull
import com.asfine.mythought.data.model.Thought
import com.asfine.mythought.ui.components.AppTopBar
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ThoughtDetailScreen(
    thought: Thought,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit
) {
    val date = thought.createdAt.toJakartaDateTimeOrNull()
    var isEditing by remember {
        mutableStateOf(false)
    }

    var content by remember {
        mutableStateOf(thought.content)
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Catatan",
                subtitle = date?.format(
                    DateTimeFormatter.ofPattern(
                        "EEEE, dd MMM yyyy • HH:mm",
                        Locale.forLanguageTag("id-ID")
                    )
                ) ?: "Tanggal tidak valid"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= 5000) {
                            content = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 10
                )
            } else {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(
                Modifier.height(40.dp)
            )
            if (!isEditing) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        isEditing = true
                    }
                ) {
                    Icon(Icons.Outlined.Edit, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit")
                }
            }
            Spacer(
                Modifier.height(12.dp)
            )
            if (isEditing) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onUpdate(content)
                        isEditing = false
                    }
                ) {
                    Text("Simpan")
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        content = thought.content
                        isEditing = false
                    }
                ) {
                    Text("Batal")
                }
                Spacer(Modifier.height(12.dp))
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                onClick = onDelete
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    null
                )
                Spacer(
                    Modifier.width(8.dp)
                )
                Text(
                    "Hapus",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
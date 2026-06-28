package com.asfine.mythought.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asfine.mythought.ui.components.AppTopBar
import com.asfine.mythought.ui.components.PrimaryButton
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WriteThoughtScreen(
    isSaving: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
    onSave: (String) -> Unit
) {

    var content by remember {
        mutableStateOf("")
    }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM yyyy",
            Locale.forLanguageTag("id-ID")
        )
    }

    val timeFormatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    Scaffold(

        topBar = {

            AppTopBar(
                title = "MyThought",
                subtitle = "Apa yang sedang kamu pikirkan hari ini?"
            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())

        ) {

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Hari ini",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = LocalDate.now().format(dateFormatter),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = LocalTime.now().format(timeFormatter) + " WIB",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(20.dp)
                ) {

                    if (content.isBlank()) {

                        Text(
                            text = "Tulis apa pun...\n\nTidak ada yang akan menghakimi pikiranmu.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    }

                    BasicTextField(

                        value = content,

                        onValueChange = {
                            onClearError()

                            if (it.length <= 5000)
                                content = it

                        },

                        modifier = Modifier.fillMaxSize(),

                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),

                        cursorBrush = SolidColor(
                            MaterialTheme.colorScheme.primary
                        )

                    )

                }

            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = "${content.length} / 5000 karakter",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            PrimaryButton(

                text = if (isSaving) "Menyimpan..." else "Simpan Pikiran",

                enabled = content.isNotBlank() && !isSaving,

                onClick = {
                    onClearError()

                    onSave(content)

                }

            )

            Spacer(Modifier.height(32.dp))

        }

    }

}
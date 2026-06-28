package com.asfine.mythought.ui.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.asfine.mythought.core.Config.APP_NAME

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    photoUrl: String?,
    isFingerprintEnabled: Boolean,
    onFingerprintToggle: (Boolean) -> Unit,
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val isBiometricAvailable = remember(context) {
        isBiometricReady(context)
    }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { androidx.compose.material3.Text("Tentang Aplikasi") },
            text = {
                androidx.compose.material3.Text(
                    "$APP_NAME membantu kamu menulis dan menyimpan pikiran harian secara aman."
                )
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }) {
                    androidx.compose.material3.Text("Tutup")
                }
            }
        )
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.Text(
                text = "Profil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto profil Google",
                            modifier = Modifier
                                .height(64.dp)
                                .fillMaxWidth(0.2f)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Foto profil default",
                            modifier = Modifier.height(64.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        androidx.compose.material3.Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        androidx.compose.material3.Text(
                            text = "" +
                                    "$email",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            ProfileSwitchItem(
                title = "Fingerprint",
                subtitle = if (isBiometricAvailable) {
                    if (isFingerprintEnabled) "Aktif" else "Nonaktif"
                } else {
                    "Tidak tersedia di perangkat"
                },
                checked = isFingerprintEnabled && isBiometricAvailable,
                enabled = isBiometricAvailable,
                onCheckedChange = onFingerprintToggle
            )

            ProfileSwitchItem(
                title = "Dark Mode",
                subtitle = if (isDarkModeEnabled) "Aktif" else "Nonaktif",
                checked = isDarkModeEnabled,
                onCheckedChange = onDarkModeToggle
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showAboutDialog = true }
            ) {
                androidx.compose.material3.Text(
                    text = "Tentang Aplikasi",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.material3.Text("Logout")
            }
        }
    }
}

@Composable
private fun ProfileSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                androidx.compose.material3.Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                androidx.compose.material3.Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

private fun isBiometricReady(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    val result = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_WEAK
    )
    return result == BiometricManager.BIOMETRIC_SUCCESS
}
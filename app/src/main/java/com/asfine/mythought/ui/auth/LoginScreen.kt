package com.asfine.mythought.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.asfine.mythought.core.Config.APP_NAME
import com.asfine.mythought.core.GoogleConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import androidx.credentials.GetCredentialResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    isSigningIn: Boolean,
    authError: String?,
    onGoogleLogin: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isAttempting by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🧠",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(
                Modifier.height(16.dp)
            )
            Text(
                text = APP_NAME,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(
                Modifier.height(8.dp)
            )
            Text(
                "Masuk untuk mulai menulis."
            )
            val activeError = loginError ?: authError
            if (!activeError.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = activeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(
                Modifier.height(32.dp)
            )
            suspend fun requestGoogleCredential(
                filterByAuthorizedAccounts: Boolean
            ): GetCredentialResponse {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                    .setServerClientId(GoogleConfig.WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                return credentialManager.getCredential(context = context, request = request)
            }

            // NoCredentialException can be a transient Play Services/network blip even
            // when a Google account exists, so retry across both account filters before
            // surfacing an error.
            suspend fun requestGoogleCredentialWithRetry(): GetCredentialResponse {
                return try {
                    requestGoogleCredential(filterByAuthorizedAccounts = true)
                } catch (_: NoCredentialException) {
                    try {
                        requestGoogleCredential(filterByAuthorizedAccounts = false)
                    } catch (_: NoCredentialException) {
                        delay(500)
                        requestGoogleCredential(filterByAuthorizedAccounts = false)
                    }
                }
            }

            val attemptGoogleSignIn: () -> Unit = {
                coroutineScope.launch {
                    loginError = null
                    isAttempting = true
                    try {
                        val result = requestGoogleCredentialWithRetry()
                        val credential = result.credential

                        if (
                            credential is CustomCredential &&
                            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        ) {
                            val googleCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            onGoogleLogin(googleCredential.idToken)
                        } else {
                            loginError = "Tipe kredensial tidak didukung."
                        }
                    } catch (_: NoCredentialException) {
                        loginError = "Akun Google tidak ditemukan di perangkat ini."
                    } catch (_: GoogleIdTokenParsingException) {
                        loginError = "Gagal membaca token Google."
                    } catch (_: GetCredentialException) {
                        loginError = "Login Google dibatalkan atau gagal."
                    } catch (e: Exception) {
                        loginError = e.message ?: "Terjadi kesalahan saat login Google."
                    } finally {
                        isAttempting = false
                    }
                }
            }

            val isBusy = isSigningIn || isAttempting

            Button(
                enabled = !isBusy,
                onClick = attemptGoogleSignIn
            ) {
                if (isBusy) {
                    CircularProgressIndicator()
                } else {
                    Text(if (activeError.isNullOrBlank()) "Masuk dengan Google" else "Coba Lagi")
                }
            }
        }
    }
}
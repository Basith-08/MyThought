package com.asfine.mythought.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asfine.mythought.auth.AuthState
import com.asfine.mythought.ui.auth.LoginScreen
import com.asfine.mythought.ui.auth.SplashScreen
import com.asfine.mythought.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    isFingerprintEnabled: Boolean,
    onFingerprintToggle: (Boolean) -> Unit
) {
    val authVm: AuthViewModel = viewModel()
    when (authVm.authState) {
        AuthState.Loading -> {
            SplashScreen()
        }
        AuthState.LoggedOut -> {
            LoginScreen(
                isSigningIn = authVm.isSigningIn,
                authError = authVm.authError,
                onGoogleLogin = { idToken ->
                    authVm.signIn(idToken)
                }
            )
        }
        AuthState.LoggedIn -> {
            MainNavigation(
                authVm = authVm,
                isDarkModeEnabled = isDarkModeEnabled,
                onDarkModeToggle = onDarkModeToggle,
                isFingerprintEnabled = isFingerprintEnabled,
                onFingerprintToggle = onFingerprintToggle
            )
        }
    }
}
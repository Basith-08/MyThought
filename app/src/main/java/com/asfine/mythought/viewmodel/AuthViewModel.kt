package com.asfine.mythought.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asfine.mythought.auth.AuthState
import com.asfine.mythought.data.model.UserProfile
import com.asfine.mythought.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    var authState by mutableStateOf<AuthState>(
        AuthState.Loading
    )
        private set

    var isSigningIn by mutableStateOf(false)
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    var profile by mutableStateOf<UserProfile?>(null)
        private set

    init {
        checkSession()
    }
    fun signIn(
        idToken: String
    ) {
        viewModelScope.launch {
            isSigningIn = true
            try {
                authError = null
                repository.signInWithGoogle(idToken)
                profile = repository.getCurrentUserProfile()
                authState = AuthState.LoggedIn
            } catch (e: Exception) {
                authState = AuthState.LoggedOut
                authError = e.message ?: "Login Google gagal. Coba lagi."
            } finally {
                isSigningIn = false
            }
        }
    }
    fun checkSession() {
        viewModelScope.launch {
            authState =
                if (repository.isLoggedIn()) {
                    profile = repository.getCurrentUserProfile()
                    AuthState.LoggedIn
                } else {
                    profile = null
                    AuthState.LoggedOut
                }
        }
    }
    fun logout(context: Context) {
        viewModelScope.launch {
            repository.logout()
            try {
                CredentialManager.create(context)
                    .clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) {
            }
            authState = AuthState.LoggedOut
            authError = null
            profile = null
        }
    }
}
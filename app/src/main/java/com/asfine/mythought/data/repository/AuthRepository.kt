package com.asfine.mythought.data.repository

import com.asfine.mythought.data.SupabaseProvider
import com.asfine.mythought.data.model.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class AuthRepository {
    private val client = SupabaseProvider.client

    suspend fun signInWithGoogle(
        idToken: String
    ) {
        client.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Google
        }
    }
    suspend fun isLoggedIn(): Boolean {
        client.auth.awaitInitialization()
        return client.auth.currentSessionOrNull() != null
    }
    suspend fun logout() {
        client.auth.signOut()
    }

    fun hasSession(): Boolean {
        return client.auth.currentSessionOrNull() != null
    }

    fun getCurrentUserProfile(): UserProfile? {
        val user = client.auth.currentSessionOrNull()?.user ?: return null
        val metadata = user.userMetadata as? JsonObject

        val fullName = metadata
            ?.get("full_name")
            ?.jsonPrimitive
            ?.contentOrNull
            ?.takeIf { it.isNotBlank() }
            ?: metadata
                ?.get("name")
                ?.jsonPrimitive
                ?.contentOrNull
                ?.takeIf { it.isNotBlank() }
            ?: user.email
            ?: "Pengguna"

        val avatarUrl = metadata
            ?.get("avatar_url")
            ?.jsonPrimitive
            ?.contentOrNull
            ?.takeIf { it.isNotBlank() }
            ?: metadata
                ?.get("picture")
                ?.jsonPrimitive
                ?.contentOrNull
                ?.takeIf { it.isNotBlank() }

        return UserProfile(
            name = fullName,
            email = user.email ?: "-",
            photoUrl = avatarUrl
        )
    }
}
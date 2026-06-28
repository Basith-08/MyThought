package com.asfine.mythought.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asfine.mythought.data.model.Thought
import com.asfine.mythought.data.repository.ThoughtRepository
import kotlinx.coroutines.launch

class ThoughtViewModel : ViewModel() {
    private val repository = ThoughtRepository()
    var thoughts by mutableStateOf<List<Thought>>(emptyList())
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    init {
        loadThoughts()
    }
    fun loadThoughts() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                errorMessage = null
                thoughts = repository.getThoughts()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isRefreshing = false
            }
        }
    }
    fun saveThought(
        content: String,
        onSuccess: (() -> Unit)? = null
    ) {
        if (content.isBlank()) return

        viewModelScope.launch {
            isSaving = true
            try {
                errorMessage = null
                repository.saveThought(content)
                loadThoughts()

                onSuccess?.invoke()

            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isSaving = false
            }
        }
    }
    fun clearError() {
        errorMessage = null
    }
}
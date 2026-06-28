package com.asfine.mythought.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asfine.mythought.data.model.Thought
import com.asfine.mythought.data.repository.ThoughtRepository
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val repository = ThoughtRepository()
    var thought by mutableStateOf<Thought?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load(id: String) {
        viewModelScope.launch {
            try {
                errorMessage = null
                thought = repository.getThoughtById(id)
            } catch (e: Exception) {
                errorMessage = e.message
                thought = null
            }
        }
    }
    fun deleteThought(
        id: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                errorMessage = null
                repository.deleteThought(id)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
    fun updateThought(
        id: String,
        content: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                errorMessage = null
                repository.updateThought(
                    id,
                    content
                )
                load(id)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
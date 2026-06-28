package com.asfine.mythought.ui.history

sealed class HistoryItem {
    data class Header(
        val title: String
    ) : HistoryItem()
    data class Thought(
        val id: String?,
        val content: String,
        val createdAt: String?
    ) : HistoryItem()
}
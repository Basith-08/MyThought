package com.asfine.mythought.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Thought(
    val id: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String
)
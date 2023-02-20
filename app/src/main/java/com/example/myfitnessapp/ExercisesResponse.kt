package com.example.myfitnessapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExercisesResponse(
    val count: Long,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Exercises>
)

@Serializable
data class Exercises(
    val id: Long,
    val name: String,
    val description: String? = null,
    val muscles: List<Long>? = null,
    @SerialName("muscles_secondary")
    val musclesSecondary: List<Long>? = null,
)
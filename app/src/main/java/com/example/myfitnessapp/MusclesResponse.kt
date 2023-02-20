package com.example.myfitnessapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MusclesResponse(
    val count: Long,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Muscles>
)

@Serializable
data class Muscles(
    val id: Long,
    val uuid: String? = null,
    val name: String,
    @SerialName("name_en")
    val nameEn: String,

    val muscles: List<Long>? = null,

    @SerialName("muscles_secondary")
    val musclesSecondary: List<Long>? = null,


    )
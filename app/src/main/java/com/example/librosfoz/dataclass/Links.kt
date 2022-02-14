package com.example.librosfoz.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class Links(
    val self: String,
    val related: String = ""
): java.io.Serializable

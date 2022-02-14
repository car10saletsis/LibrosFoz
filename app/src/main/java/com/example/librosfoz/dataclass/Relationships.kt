package com.example.librosfoz.dataclass

import kotlinx.serialization.Serializable

@Serializable
data class Relationships(
    val authors: Authors,
    val categories: Categories
): java.io.Serializable

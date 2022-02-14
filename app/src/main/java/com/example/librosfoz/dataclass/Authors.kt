package com.example.librosfoz.dataclass

import kotlinx.serialization.Serializable


@Serializable
data class Authors(
    val links: Links
): java.io.Serializable

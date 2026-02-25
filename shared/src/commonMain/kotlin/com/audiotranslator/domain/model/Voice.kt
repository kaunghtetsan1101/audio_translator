package com.audiotranslator.domain.model

data class Voice(
    val id: String,
    val name: String,
    val gender: String? = null
)

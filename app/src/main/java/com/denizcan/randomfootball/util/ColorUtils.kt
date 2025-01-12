package com.denizcan.randomfootball.util

import androidx.compose.ui.graphics.Color

fun String.toComposeColor(): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: IllegalArgumentException) {
        Color.Gray // Fallback renk
    }
} 
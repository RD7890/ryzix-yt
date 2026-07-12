package com.rohan.ryzixyt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Ryzix YT is a dark-first, flat, premium surface. No gradients, no dynamic color.
private val RyzixDarkScheme = darkColorScheme(
    primary = RyzixPrimary,
    onPrimary = RyzixOnPrimary,
    background = RyzixBackground,
    onBackground = RyzixOnSurface,
    surface = RyzixSurface,
    onSurface = RyzixOnSurface,
    surfaceVariant = RyzixSurfaceAlt,
    onSurfaceVariant = RyzixOnSurfaceMuted,
    outline = RyzixOutline,
)

@Composable
fun RyzixYTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Ryzix YT is deliberately dark-first for a premium, cinematic feel — the same
    // flat scheme is used regardless of system theme to keep the brand consistent.
    val colorScheme = RyzixDarkScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RyzixTypography,
        content = content,
    )
}

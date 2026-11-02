package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = JadeGreenPrimary,
    onPrimary = BoneWhiteBg,
    primaryContainer = JadeGreenDark,
    onPrimaryContainer = JadeGreenLight,
    secondary = GoldenWheatAccent,
    onSecondary = DarkCharcoalBg,
    secondaryContainer = GoldenWheatDark,
    onSecondaryContainer = GoldenWheatLight,
    background = DarkCharcoalBg,
    onBackground = TextDarkPrimary,
    surface = DarkCharcoalSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = DarkCharcoalSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = DarkCharcoalOutline
)

private val LightColorScheme = lightColorScheme(
    primary = JadeGreenPrimary,
    onPrimary = BoneWhiteSurface,
    primaryContainer = JadeGreenLight,
    onPrimaryContainer = JadeGreenDark,
    secondary = GoldenWheatAccent,
    onSecondary = BoneWhiteSurface,
    secondaryContainer = GoldenWheatLight,
    onSecondaryContainer = GoldenWheatDark,
    background = BoneWhiteBg,
    onBackground = TextLightPrimary,
    surface = BoneWhiteSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = BoneWhiteSurfaceVariant,
    onSurfaceVariant = TextLightSecondary,
    outline = BoneWhiteOutline
)

@Composable
fun DamBazarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PersianTypography,
            content = content
        )
    }
}

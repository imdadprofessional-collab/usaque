package com.cdlpermitprep.usa.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightScheme = lightColorScheme(
    primary = CdlColors.Ink,
    onPrimary = CdlColors.Cream,
    secondary = CdlColors.Pink,
    tertiary = CdlColors.Purple,
    background = CdlColors.Cream,
    onBackground = CdlColors.Ink,
    surface = CdlColors.SurfaceLight,
    onSurface = CdlColors.Ink,
    error = CdlColors.Danger,
)

private val DarkScheme = darkColorScheme(
    primary = CdlColors.Cream,
    onPrimary = CdlColors.Ink,
    secondary = CdlColors.Pink,
    tertiary = CdlColors.Purple,
    background = CdlColors.CreamDark,
    onBackground = CdlColors.Cream,
    surface = CdlColors.SurfaceDark,
    onSurface = CdlColors.Cream,
    error = CdlColors.Danger,
)

/** Exposes the bold outline color used throughout the neo-brutalist card style. */
val androidx.compose.material3.ColorScheme.cardBorder: Color
    @Composable get() = if (isSystemInDarkTheme()) CdlColors.BorderDark else CdlColors.Border

@Composable
fun CdlPermitPrepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkScheme else LightScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as? android.app.Activity)?.window
        window?.let {
            it.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CdlTypography,
        content = content,
    )
}

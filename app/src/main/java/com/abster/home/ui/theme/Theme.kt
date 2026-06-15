package com.abster.home.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = IndigoBlue,
    secondary = IndigoBlue.copy(alpha = 0.7f),
    tertiary = MintGreen,
    background = DeepObsidian,
    surface = SlateGray,
    onPrimary = DeepObsidian,
    onSecondary = DeepObsidian,
    onTertiary = DeepObsidian,
    onBackground = TextHighEmphasis,
    onSurface = TextHighEmphasis,
    surfaceVariant = SlateGray,
    onSurfaceVariant = TextMediumEmphasis
)

@Composable
fun HomeTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

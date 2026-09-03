package com.pedro.ChamaKids.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ChamaKidsBlue,
    secondary = ChamaKidsHeaderGray,
    background = ChamaKidsBlue,
    surface = ChamaKidsHeaderGray
)

private val LightColorScheme = lightColorScheme(
    primary = ChamaKidsBlue,
    secondary = ChamaKidsHeaderGray,
    background = ChamaKidsBlue,
    surface = ChamaKidsHeaderGray
)

@Composable
fun ChamaKidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

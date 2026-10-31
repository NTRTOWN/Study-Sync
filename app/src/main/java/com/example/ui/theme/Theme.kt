package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val StudySphereDarkColorScheme = darkColorScheme(
    primary = PrimaryIndigoLight,
    onPrimary = DeepNavy,
    primaryContainer = PrimaryIndigoDark,
    onPrimaryContainer = Color.White,
    secondary = SecondaryCyan,
    onSecondary = DeepNavy,
    secondaryContainer = CardNavy,
    onSecondaryContainer = CyanGlow,
    tertiary = TertiaryAmber,
    onTertiary = DeepNavy,
    tertiaryContainer = AmberGlow,
    onTertiaryContainer = DeepNavy,
    background = MidnightDark,
    onBackground = TextPrimary,
    surface = DeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    outline = BorderNavy
)

private val StudySphereLightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = PrimaryIndigoDark,
    secondary = SecondaryCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF0E7490),
    tertiary = TertiaryAmber,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun StudySphereTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to prioritize rich custom study branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> StudySphereDarkColorScheme
        else -> StudySphereLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    StudySphereTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}

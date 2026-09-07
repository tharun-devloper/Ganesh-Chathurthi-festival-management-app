package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme =
  lightColorScheme(
    primary = RoyalPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = RoyalPurpleDark,
    secondary = PurpleAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = RoyalPurpleDark,
    tertiary = FestivalGold,
    onTertiary = Color(0xFF451A03),
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    error = ExpenseRed,
    onError = Color.White,
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = RoyalPurpleDark,
    primaryContainer = RoyalPurplePrimary,
    onPrimaryContainer = Color.White,
    secondary = FestivalGold,
    onSecondary = Color.Black,
    background = DarkNavySplash,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkNavySplashCard,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color.Black,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = RoyalPurplePrimary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      }
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

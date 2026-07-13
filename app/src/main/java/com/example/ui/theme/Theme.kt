package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.drawBehind

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = TertiaryBlue,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    error = ErrorRed,
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryBlue,
    tertiary = TertiaryBlue,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    error = ErrorRed,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to preserve our custom frosted glass theme
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography) {
    androidx.compose.material3.Surface(
      modifier = androidx.compose.ui.Modifier.fillMaxSize(),
      color = Color.Transparent
    ) {
      androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier
          .fillMaxSize()
          .frostedBackground(darkTheme)
      ) {
        content()
      }
    }
  }
}

private fun androidx.compose.ui.Modifier.frostedBackground(isDark: Boolean): androidx.compose.ui.Modifier = this.drawBehind {
  // 1. Draw base gradient background
  val baseBrush = if (isDark) {
    androidx.compose.ui.graphics.Brush.verticalGradient(
      colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
    )
  } else {
    androidx.compose.ui.graphics.Brush.verticalGradient(
      colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))
    )
  }
  drawRect(brush = baseBrush)

  // 2. Draw colorful glowing ambient circles to simulate refracted light through frosted glass
  if (isDark) {
    // Soft deep blue glow in top right
    drawCircle(
      color = Color(0xFF1D4ED8).copy(alpha = 0.25f),
      radius = size.width * 0.75f,
      center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.15f)
    )
    // Soft violet/indigo glow in bottom left
    drawCircle(
      color = Color(0xFF6D28D9).copy(alpha = 0.2f),
      radius = size.width * 0.65f,
      center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.85f)
    )
    // Accent teal/cyan glow near center
    drawCircle(
      color = Color(0xFF0D9488).copy(alpha = 0.12f),
      radius = size.width * 0.45f,
      center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.45f)
    )
  } else {
    // Soft bright blue glow in top right
    drawCircle(
      color = Color(0xFF93C5FD).copy(alpha = 0.4f),
      radius = size.width * 0.75f,
      center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.15f)
    )
    // Soft purple/lavender glow in bottom left
    drawCircle(
      color = Color(0xFFDDD6FE).copy(alpha = 0.35f),
      radius = size.width * 0.65f,
      center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.85f)
    )
    // Accent soft emerald/mint glow near center
    drawCircle(
      color = Color(0xFFA7F3D0).copy(alpha = 0.25f),
      radius = size.width * 0.45f,
      center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.45f)
    )
  }
}

val GlassCardShape = RoundedCornerShape(24.dp)

@Composable
fun glassCardBorder(): BorderStroke {
  val isDark = isSystemInDarkTheme()
  return BorderStroke(
    width = 1.dp,
    color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.45f)
  )
}

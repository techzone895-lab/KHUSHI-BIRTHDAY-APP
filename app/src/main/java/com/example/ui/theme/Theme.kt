package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BrightPink,
    secondary = WarmGold,
    tertiary = PastelPink,
    background = DarkBackground,
    surface = DarkBackground,
    onPrimary = SoftWhite,
    onBackground = PastelPink,
    onSurface = PastelPink
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrightPink,
    secondary = WarmGold,
    tertiary = PastelPink,
    background = PastelPink,
    surface = SoftWhite,
    onPrimary = SoftWhite,
    onBackground = DarkPink,
    onSurface = DarkPink
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

package com.bitchat.android.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

// Liquid Glass Theme

private val LiquidLightColorScheme = lightColorScheme(
    primary = LiquidBlue,
    onPrimary = Color.White,
    secondary = LiquidBlue,
    onSecondary = Color.White,
    background = LiquidWhite,
    onBackground = LiquidBlack,
    surface = LiquidGlassLight,     // Semi-transparent
    onSurface = LiquidBlack,
    surfaceVariant = LiquidGlassLight,
    onSurfaceVariant = LiquidBlack,
    error = Color(0xFFFF3B30),
    outline = LiquidBlue            // Blue borders
)

private val LiquidDarkColorScheme = darkColorScheme(
    primary = LiquidIceBlue,
    onPrimary = Color.Black,
    secondary = LiquidIceBlue,
    onSecondary = Color.Black,
    background = LiquidBlack,       // Amoled Black
    onBackground = LiquidTextWhite,
    surface = LiquidGlassDark,      // Semi-transparent
    onSurface = LiquidTextWhite,
    surfaceVariant = LiquidGlassDark,
    onSurfaceVariant = LiquidTextWhite,
    error = Color(0xFFFF453A),
    outline = LiquidIceBlue         // Ice Blue borders
)

@Composable
fun BitchatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LiquidDarkColorScheme else LiquidLightColorScheme

    val view = LocalView.current
    SideEffect {
        (view.context as? Activity)?.window?.let { window ->
            // Transparent status/nav bars to let background show through
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            val isLight = !darkTheme
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    if (isLight) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                val flags = if (isLight) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
                window.decorView.systemUiVisibility = flags
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
             // Global Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                content()
            }
        }
    )
}

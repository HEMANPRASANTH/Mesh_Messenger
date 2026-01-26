package com.bitchat.android.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class ThemeControl(val currentMode: Int, val onThemeChanged: (Int) -> Unit)

val LocalThemeControl = staticCompositionLocalOf { ThemeControl(0) {} }

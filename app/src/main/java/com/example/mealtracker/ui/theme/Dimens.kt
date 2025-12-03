package com.example.mealtracker.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,

    val screenPadding: Dp = 16.dp,
    val cardElevation: Dp = 2.dp,
    val iconSize: Dp = 24.dp,
    val listSpacing: Dp = 16.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
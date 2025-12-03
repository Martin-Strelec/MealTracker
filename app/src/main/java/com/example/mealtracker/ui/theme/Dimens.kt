package com.example.mealtracker.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimens(
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,

    val listImageHeight: Dp = 100.dp,
    val detailImageHeight: Dp = 300.dp,
    val inputImageHeight: Dp = 200.dp,
    val placeholderImageHeight: Dp = 150.dp,

    val spacerSmall: Dp = 8.dp,
    val spacerMedium: Dp = 12.dp,

    val screenPadding: Dp = 16.dp,
    val cardElevation: Dp = 2.dp,
    val iconSize: Dp = 24.dp,
    val listSpacing: Dp = 16.dp
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
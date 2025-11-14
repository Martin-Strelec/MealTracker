package com.example.mealtracker.ui.tracked

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mealtracker.R
import com.example.mealtracker.ui.navigation.NavigationDestination

object TrackingDestination : NavigationDestination {
    override val route = "tracking"
    override val titleRes = R.string.tracking
    override val icon = Icons.Filled.Star
    override val showInDrawer = true
}

@Composable
fun TrackingScreen(
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Tracking Screen")
    }
}
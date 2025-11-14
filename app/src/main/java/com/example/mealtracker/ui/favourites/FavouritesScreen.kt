package com.example.mealtracker.ui.favourites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mealtracker.R
import com.example.mealtracker.ui.navigation.NavigationDestination

object FavouriteDestination : NavigationDestination {
    override val route = "favourite"
    override val titleRes = R.string.favourites
    override val icon = Icons.Filled.Favorite
    override val showInDrawer = true
    override val topLevel = true
}

@Composable
fun FavouritesScreen(
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Favourites Screen")
    }
}
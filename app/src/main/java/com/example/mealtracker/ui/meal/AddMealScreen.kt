package com.example.mealtracker.ui.meal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.R

object AddMealDestination : NavigationDestination {
    override val route = "add_meal"
    override val titleRes = R.string.add_meal
    override val icon = Icons.Filled.Add
    override val showInDrawer = false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Add Meal Screen")
    }
}
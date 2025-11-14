package com.example.mealtracker.ui.meal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.mealtracker.R
import com.example.mealtracker.ui.navigation.NavigationDestination

object EditMealDestination : NavigationDestination {
    override val route = "edit_meal"
    override val titleRes = R.string.edit_meal
    override val icon = Icons.Filled.Edit
    override val showInDrawer = false
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Favourites Screen")
    }
}
package com.example.mealtracker.ui.favourites

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.R
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.home.HomeBody
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.theme.AppTheme

/**
 * Navigation destination object for the Favourites Screen.
 * Defined here to be used in the NavHost graph.
 */
object FavouriteDestination : NavigationDestination {
    override val route = "favourite"
    override val titleRes = R.string.favourites
    override val icon = Icons.Filled.Favorite
    override val showInDrawer = true
}

/**
 * Composable that displays the list of user-favourited meals.
 * Includes a search bar to filter through the favourites.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavouritesScreen(
    navigateToMealDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavouritesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collect UI state (list of meals) and search query state from ViewModel
    val favouritesUiState by viewModel.favouritesUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold { innerPadding ->
        Surface (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column (
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // --- Search Bar ---
                // Allows filtering the list of favourites locally
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimens.paddingLarge),
                    placeholder = { Text(stringResource(R.string.search_bar_meal)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_icon))
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        // Hide the underline indicator for a cleaner 'floating' look
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                // --- Meal List ---
                // Reuses HomeBody from the Home package to display the list of meals.
                // This ensures consistent look and feel between Home and Favourites screens.
                HomeBody(
                    mealList = favouritesUiState.mealList,
                    onMealClick = navigateToMealDetail,
                    emptyText = stringResource(R.string.no_favourite_meals_description),
                    modifier = Modifier
                )
            }
        }
    }
}
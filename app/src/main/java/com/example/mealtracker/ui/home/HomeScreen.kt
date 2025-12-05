package com.example.mealtracker.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mealtracker.R
import com.example.mealtracker.data.Meal
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.theme.AppTheme

/**
 * Navigation destination object for the Home Screen.
 * Contains route information and UI assets (icon/title) for the Navigation Drawer.
 */
object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.meals
    override val icon = Icons.Filled.Home
    override val showInDrawer = true
}

/**
 * The main Composable for the Home Screen.
 * Orchestrates the UI structure including the Scaffold, FAB, Search Bar, and the Meal List.
 *
 * @param navigateToAddMeal Callback to navigate to the "Add Meal" screen.
 * @param navigateToMealDetail Callback to navigate to the details of a specific meal.
 * @param viewModel The ViewModel that holds the state for this screen.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddMeal: () -> Unit,
    navigateToMealDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collect the UI state (list of meals) and the current search query from the ViewModel.
    // using collectAsState ensures the UI recomposes whenever the data changes.
    val homeUiState by viewModel.homeUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        // Floating Action Button (FAB) positioned at the bottom-right.
        // Used to trigger the navigation to the "Add Meal" screen.
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddMeal,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_meal))
            }
        },
    ) { innerPadding ->
        // Surface acts as the background container for the screen content.
        Surface (
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ){
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                // --- Search Bar Section ---
                // A TextField that updates the search query in the ViewModel as the user types.
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
                        // Transparent indicators create a "floating" search bar look without underlines.
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                // --- Main Body Section ---
                // Displays the list of meals or an empty state message.
                HomeBody(
                    mealList = homeUiState.mealList,
                    onMealClick = navigateToMealDetail,
                    emptyText = stringResource(R.string.no_meals_description),
                    modifier = Modifier
                )
            }
        }
    }
}

/**
 * Composable that switches between the list of meals and an empty state message.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeBody(
    mealList: List<Meal>,
    onMealClick: (Int) -> Unit,
    emptyText: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (mealList.isEmpty()) {
            // Show this text if the database is empty or the search yielded no results.
            Text(
                text = emptyText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(AppTheme.dimens.paddingLarge)
            )
        } else {
            // Show the LazyColumn list if items exist.
            MealList(
                mealList = mealList,
                onItemClick = { onMealClick(it.id) },
                contentPadding = PaddingValues(),
                modifier = Modifier.padding(AppTheme.dimens.paddingLarge)
            )
        }
    }
}

/**
 * A wrapper around LazyColumn to display the list of meals efficiently.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealList(
    mealList: List<Meal>,
    onItemClick: (Meal) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        // Iterate through the meal list.
        // 'key' is used to help Compose intelligently recompose items when the list changes.
        items(items = mealList, key = {it.id}) { item ->
            InventoryItem(
                item = item,
                modifier = Modifier
                    .padding(bottom = AppTheme.dimens.paddingMedium)
                    .clickable { onItemClick(item) } // Handle click events on the specific card
            )
        }
    }
}

/**
 * Represents a single row item (Card) in the meal list.
 * Displays the meal image, name, and calorie count.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InventoryItem(
    item: Meal,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.dimens.cardElevation),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimens.listItemHeight),
        ) {
            // Load the image asynchronously using Coil.
            AsyncImage(
                model = item.image,
                contentDescription = stringResource(R.string.meal_image),
                modifier = Modifier
                    .width(AppTheme.dimens.listImageSize)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Text details column.
            Column (
                modifier = Modifier
                    .padding(AppTheme.dimens.paddingMedium),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${item.calories} cal",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    AppTheme {
        HomeBody(listOf(
            Meal(1, "Brambor", "S", "Super", 5, System.currentTimeMillis(), isFavourite = false , isTracked = false),
            Meal(2, "Mrkev", "S", "Orange", 12, System.currentTimeMillis(),isFavourite = false , isTracked = false),
            Meal(3, "Cibule", "S", "White", 15, System.currentTimeMillis(), isFavourite = false , isTracked = false)
        ), onMealClick = {},
            emptyText = "No meals found")
    }
}
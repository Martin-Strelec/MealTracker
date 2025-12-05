package com.example.mealtracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealtracker.R.string
import com.example.mealtracker.ui.favourites.FavouriteDestination
import com.example.mealtracker.ui.home.HomeDestination
import com.example.mealtracker.ui.navigation.MealTrackerNavHost
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.tracked.TrackingDestination
import kotlinx.coroutines.launch

/**
 * The top-level Composable for the Meal Tracker application.
 * It sets up the persistent UI elements like the Navigation Drawer and the Top App Bar,
 * and hosts the main content area via [MealTrackerNavHost].
 *
 * @param navController The NavHostController to manage app navigation.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealTrackerApp(navController: NavHostController = rememberNavController()) {
    // List of destinations that should appear in the side drawer.
    val navigationDestinations = listOf(HomeDestination, TrackingDestination, FavouriteDestination)

    // State to manage the open/closed status of the drawer.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Observe the current back stack entry to update the UI based on the active screen.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Find the current destination object based on the route.
    val currentDestination = navigationDestinations.find { it.route == currentRoute }
        ?: HomeDestination

    // Local state for the TopBar title.
    // It defaults to the destination's title but can be overridden by individual screens (e.g., MealDetails).
    var topBarTitle by remember { mutableStateOf("") }

    // Reset the title to the default destination title whenever the route changes.
    LaunchedEffect(currentRoute) {
        topBarTitle = ""
    }

    // Determine the actual title to display.
    val titleToDisplay = topBarTitle.ifEmpty {
        // Fallback to the resource string defined in the destination object.
        val titleRes = navigationDestinations.find { it.route == currentRoute }?.titleRes
            ?: HomeDestination.titleRes
        stringResource(titleRes)
    }

    // --- Navigation Drawer Setup ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                // Iterate through the defined destinations to create drawer items.
                navigationDestinations.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.titleRes)) },
                        selected = currentRoute == item.route, // Highlight if active
                        onClick = {
                            // Close drawer and navigate
                            scope.launch { drawerState.close() }
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid a large stack
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
    ) {
        // --- Main Screen Content ---
        Scaffold(
            topBar = {
                MealTrackerAppBar(
                    title = titleToDisplay,
                    // Show "Back" arrow if we are not on one of the main drawer screens.
                    canNavigateBack = navController.previousBackStackEntry != null && !navigationDestinations.any { it.route == currentRoute },
                    onNavigateUp = { navController.navigateUp() },
                    onMenuClicked = {
                        // Open the drawer when the menu icon is clicked.
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { innerPadding ->
            // The NavHost handles the swapping of different screens.
            // It is placed inside the Scaffold's padding to respect the TopBar.
            MealTrackerNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onTitleChange = { newTitle -> topBarTitle = newTitle} // Allow screens to update the title
            )
        }
    }
}

/**
 * A custom Top App Bar implementation.
 * Displays the title and switches between a Navigation Menu icon (Hamburger) and a Back Arrow.
 *
 * @param title The text to display in the center/start of the bar.
 * @param canNavigateBack If true, shows a Back arrow. If false, shows the Menu icon.
 * @param onNavigateUp Callback for the Back arrow.
 * @param onMenuClicked Callback for the Menu icon (opens drawer).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTrackerAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    onMenuClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                // Show Back Arrow
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
            } else {
                // Show Hamburger Menu for Drawer
                IconButton(onClick = onMenuClicked) {
                    Icon(
                        imageVector = Filled.Menu,
                        contentDescription = stringResource(string.menu_button)
                    )
                }
            }
        }
    )
}
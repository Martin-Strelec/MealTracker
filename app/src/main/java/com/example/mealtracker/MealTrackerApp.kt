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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealtracker.R.string
import com.example.mealtracker.ui.favourites.FavouriteDestination
import com.example.mealtracker.ui.home.HomeDestination
import com.example.mealtracker.ui.meal.AddMealDestination
import com.example.mealtracker.ui.meal.EditMealDestination
import com.example.mealtracker.ui.meal.MealDetailsDestination
import com.example.mealtracker.ui.navigation.MealTrackerNavHost
import com.example.mealtracker.ui.theme.AppTheme
import com.example.mealtracker.ui.tracked.TrackingDestination
import kotlinx.coroutines.launch

val topLevelDestinations = listOf(
    HomeDestination,
    FavouriteDestination,
    TrackingDestination
)

val subDestinations = listOf(
    AddMealDestination,
    EditMealDestination,
    MealDetailsDestination
)

// AppScreen enum and MealTrackerAppBar should be here as defined above
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTrackerApp(navController: NavHostController = rememberNavController()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentRoute = backStackEntry?.destination?.route ?: HomeDestination.route
    val allDestinations = topLevelDestinations + subDestinations
    val currentDestination = allDestinations.find { currentRoute.startsWith(it.route) }

    var topBarTitle by remember { mutableStateOf("") }

    LaunchedEffect(currentRoute) {
        topBarTitle = ""
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = navController.previousBackStackEntry != null,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(string.app_name), modifier = Modifier.padding(AppTheme.dimens.paddingMedium))
                // Add a spacer for some top padding
                Spacer(Modifier.height(AppTheme.dimens.spacerMedium))
                // Get all screens that should be shown in the drawer
                val drawerDestinations = topLevelDestinations.filter { it.showInDrawer }
                drawerDestinations.forEach { screen ->
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(screen.titleRes)) },
                        // Highlight the item if it's the current screen
                        selected = currentRoute.startsWith(screen.route),
                        onClick = {
                            // Navigate to the screen
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large back stack as users select items.
                                popUpTo(0) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                val title = topBarTitle.ifEmpty {
                    currentDestination?.titleRes?.let { stringResource(it) }
                        ?: stringResource(string.app_name)
                }
                MealTrackerAppBar(
                    title = title,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    onNavigateUp = { navController.navigateUp() },
                    onMenuClicked = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Your NavHost now lives inside the Scaffold's content area
            MealTrackerNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onTitleChange = { newTitle -> topBarTitle = newTitle}
            )
        }
    }
}

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
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
            } else {
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
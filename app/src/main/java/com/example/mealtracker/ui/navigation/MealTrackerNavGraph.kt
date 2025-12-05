package com.example.mealtracker.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mealtracker.ui.camera.CameraScreen
import com.example.mealtracker.ui.favourites.FavouriteDestination
import com.example.mealtracker.ui.favourites.FavouritesScreen
import com.example.mealtracker.ui.home.HomeDestination
import com.example.mealtracker.ui.home.HomeScreen
import com.example.mealtracker.ui.meal.AddMealDestination
import com.example.mealtracker.ui.meal.AddMealScreen
import com.example.mealtracker.ui.meal.EditMealDestination
import com.example.mealtracker.ui.meal.EditMealScreen
import com.example.mealtracker.ui.meal.MealDetailsDestination
import com.example.mealtracker.ui.meal.MealDetailsScreen
import com.example.mealtracker.ui.tracked.TrackingDestination
import com.example.mealtracker.ui.tracked.TrackingScreen

/**
 * Defines the complete navigation graph for the MealTracker app.
 * Maps navigation routes (Strings) to specific Composable screens.
 *
 * @param navController The controller managing app navigation.
 * @param onTitleChange Callback to update the top bar title dynamically (e.g., "Details of Pizza").
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onTitleChange: (String) -> Unit = {}
) {
    // NavHost container swapping destinations based on the current route
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route, // App starts at Home
        modifier = modifier
    ) {
        // --- Home Screen ---
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToAddMeal = { navController.navigate(AddMealDestination.route) },
                navigateToMealDetail = {
                    // Navigate to details, appending the meal ID to the route
                    navController.navigate("${MealDetailsDestination.route}/$it")
                }
            )
        }

        // --- Add Meal Screen ---
        composable(route = AddMealDestination.route) { backStackEntry ->
            // Retrieve result from CameraScreen if it was visited
            val cameraResult = backStackEntry.savedStateHandle.get<String>("camera_result")
            AddMealScreen(
                navigateBack = { navController.popBackStack() },
                onCameraClick = { navController.navigate("camera_screen")},
                cameraImageUri = cameraResult
            )
        }

        // --- Tracking History Screen ---
        composable(route = TrackingDestination.route) {
            TrackingScreen(
                navigateToMealDetail = {
                    navController.navigate("${MealDetailsDestination.route}/$it")
                }
            )
        }

        // --- Favourites Screen ---
        composable(route = FavouriteDestination.route) {
            FavouritesScreen(
                navigateToMealDetail = {
                    navController.navigate("${MealDetailsDestination.route}/$it")
                })
        }

        // --- Meal Details Screen ---
        // Defines an argument 'itemId' of type Int to identify the meal
        composable(
            route = MealDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(MealDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            MealDetailsScreen(
                navigateToEditMeal = { navController.navigate("${EditMealDestination.route}/$it") },
                navigateBack = { navController.navigateUp() },
                onTitleChange = onTitleChange
            )
        }

        // --- Edit Meal Screen ---
        // Also accepts 'itemId' to load the meal for editing
        composable(
            route = EditMealDestination.routeWithArgs,
            arguments = listOf(navArgument(EditMealDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) { backStackEntry ->

            // Check for camera result (photo taken during edit)
            val cameraResult = backStackEntry.savedStateHandle.get<String>("camera_result")

            EditMealScreen(
                navigateBack = { navController.popBackStack() },
                onCameraClick = { navController.navigate("camera_screen")},
                cameraImageUri = cameraResult
            )
        }

        // --- Camera Screen ---
        // Standalone screen that captures an image and returns the URI to the previous screen
        composable(route = "camera_screen") {
            CameraScreen(
                onImageCaptured = { uri ->
                    // Save the result (URI) into the Previous Back Stack Entry's SavedStateHandle
                    // This allows the calling screen (Add or Edit) to read it upon return.
                    navController.previousBackStackEntry?.savedStateHandle?.set("camera_result", uri.toString())
                    navController.popBackStack()
                },
                onError = { }
            )
        }
    }
}
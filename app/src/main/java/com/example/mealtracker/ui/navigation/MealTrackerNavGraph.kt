package com.example.mealtracker.ui.navigation

import android.R.attr.type
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, startDestination = HomeDestination.route, modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToAddMeal = { navController.navigate(AddMealDestination.route) },
                navigateToMealDetail = {
                    navController.navigate("${MealDetailsDestination.route}/$it")
                }
            )
        }
        composable(route = AddMealDestination.route) {
            AddMealScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = TrackingDestination.route) {
            TrackingScreen()
        }
        composable(route = FavouriteDestination.route) {
            FavouritesScreen()
        }
        composable(
            route = MealDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(MealDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            MealDetailsScreen(
                navigateToEditMeal = { navController.navigate("${EditMealDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = EditMealDestination.routeWithArgs,
            arguments = listOf(navArgument(EditMealDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            EditMealScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
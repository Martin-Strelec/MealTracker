package com.example.mealtracker.ui.navigation

import android.R.attr.type
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
import com.example.mealtracker.ui.tracked.TrackingDestination
import com.example.mealtracker.ui.tracked.TrackingScreen

@Composable
fun MealTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController, startDestination = HomeDestination.route, modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(navigateToAddMeal = { navController.navigate(AddMealDestination.route) },
                navigateToMealUpdate = {
                    navController.navigate("${EditMealDestination.route}/$it")
                })
        }
        composable(route = AddMealDestination.route) {
            AddMealScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp()
                })
        }
        composable(route = FavouriteDestination.route) {
            FavouritesScreen()
        }
        composable(route = TrackingDestination.route) {
            TrackingScreen()
        }
//        composable(
//            route = EditMealDestination.routeWithArgs,
//            arguments = listOf(navArgument(MealDetailsDestination.itemIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            MealDetailsScreen(
//                navigateToEditMeal = {
//                    navController.navigate("${MealUpdateDestination.route}/$it")
//                },
//                navigateBack = { navController.navigateUp() }
//            )
//        }
//        composable(
//            route = EditMealDestination.routeWithArgs,
//            arguments = listOf(navArgument(MealUpdateDestination.itemIdArg) {
//                type = NavType.IntType
//            })
//        ) {
//            EditMealScreen(navigateBack = { navController.popBackStack() },
//                onNavigateUp = { navController.navigateUp() })
//        }
    }
}
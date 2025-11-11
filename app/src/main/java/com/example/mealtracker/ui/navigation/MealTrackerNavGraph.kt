package com.example.mealtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mealtracker.ui.home.HomeDestination
import com.example.mealtracker.ui.home.HomeScreen
import com.example.mealtracker.ui.meal.AddMealDestination
import com.example.mealtracker.ui.meal.AddMealScreen
import com.example.mealtracker.ui.meal.EditMealDestination

@Composable
fun MealTrackerNavHost(
    navController: NavController,
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
        composable(
            route = EditMealDestination.routeWithArgs,
            arguments = listOf(navArgument(MealDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            MealDetailsScreen(
                navigateToEditMeal = {
                    navController.navigate("${MealUpdateDestination.route}/$it")
                },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = MealUpdateDestination.routeWithArgs,
            arguments = listOf(navArgument(MealUpdateDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            EditMealScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() })
        }
    }
}
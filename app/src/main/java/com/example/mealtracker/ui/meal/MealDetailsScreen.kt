package com.example.mealtracker.ui.meal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mealtracker.R
import com.example.mealtracker.data.Meal
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.theme.TWEEN_16
import kotlinx.coroutines.launch


object MealDetailsDestination : NavigationDestination {
    override val route = "meal_details"
    override val titleRes = R.string.details_meal
    override val icon = Icons.Filled.Edit
    override val showInDrawer = false
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    navigateToEditMeal: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MealDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditMeal(uiState.value.mealDetails.id) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_item_title),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        MealDetailsBody(
            mealDetailsUiState = uiState.value,
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteMeal()
                    navigateBack()
                }
            },
            onToggleFavourite = viewModel::toggleFavourite,
            onToggleTracked = viewModel::toggleTracked,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun MealDetailsBody(
    mealDetailsUiState: MealDetailsUiState,
    onDelete: () -> Unit,
    onToggleFavourite: () -> Unit,
    onToggleTracked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(TWEEN_16),
        verticalArrangement = Arrangement.spacedBy(TWEEN_16)
    ) {
        AsyncImage(
            model = mealDetailsUiState.mealDetails.toMeal().image,
            contentDescription = "Meal Image",
            modifier = Modifier
                .fillMaxSize()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }
        MealDetails(
            meal = mealDetailsUiState.mealDetails.toMeal(), modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = { deleteConfirmationRequired = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }
        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(TWEEN_16)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            IconButton(onClick = onToggleFavourite) {
                Icon(
                    imageVector = if (mealDetailsUiState.mealDetails.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favourite",
                    tint = if (mealDetailsUiState.mealDetails.isFavourite) Color.Gray else Color.Gray
                )
            }

            // Track Button
            IconButton(onClick = onToggleTracked) {
                Icon(
                    imageVector = if (mealDetailsUiState.mealDetails.isTracked) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Toggle Tracking",
                    tint = if (mealDetailsUiState.mealDetails.isTracked) Color.Gray else Color.Gray
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealDetails(
    meal: Meal, modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(TWEEN_16),
        verticalArrangement = Arrangement.spacedBy(TWEEN_16)
    ) {
        Text(
            text = meal.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ItemDetailsValues(
                labelResID = R.string.calories,
                itemDetail = meal.calories.toString(),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.weight(1f))
            ItemDetailsValues(
                labelResID = R.string.date_added,
                itemDetail = meal.toStringDate(meal.dateAdded),
                modifier = Modifier
            )
        }
        ItemDetailsMain(
            labelResID = R.string.description,
            itemDetail = meal.description,
            modifier = Modifier
        )
    }
}

@Composable
private fun ItemDetailsMain(
    @StringRes labelResID: Int, itemDetail: String, modifier: Modifier = Modifier
) {
    Text(
        text = itemDetail,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ItemDetailsValues(
    @StringRes labelResID: Int, itemDetail: String, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(labelResID),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = itemDetail,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.delete_item)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        })
}
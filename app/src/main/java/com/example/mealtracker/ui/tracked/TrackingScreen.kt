package com.example.mealtracker.ui.tracked

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mealtracker.R
import com.example.mealtracker.data.Meal
import com.example.mealtracker.data.TrackedMealEntry
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.theme.AppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Navigation destination for the Tracking screen.
 */
object TrackingDestination : NavigationDestination {
    override val route = "tracking"
    override val titleRes = R.string.tracking
    override val icon = Icons.Filled.DateRange
    override val showInDrawer = true
}

/**
 * The main composable for the Tracking/History feature.
 * Displays a list of meals consumed on a specific date and calculates total calories.
 *
 * @param navigateToMealDetail Callback to view details of a specific meal.
 * @param viewModel The ViewModel managing the state of tracked meals and date selection.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackingScreen(
    navigateToMealDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // Collect UI state from ViewModel
    val trackingUiState by viewModel.trackingUiState.collectAsState() // Tracked meals for selected date
    val allMeals by viewModel.allMealsState.collectAsState()          // All available meals (for adding new ones)
    val selectedDate by viewModel.selectedDate.collectAsState()       // Current date filter

    // Local state for dialog visibility
    var showDatePicker by remember { mutableStateOf(false) }
    var showAddMealDialog by remember { mutableStateOf(false) }

    // State for the Material 3 Date Picker component
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    Scaffold(
        // Floating Action Button to add a meal to the current day
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMealDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_meal_tracked))
            }
        },
        // Persistent Bottom Bar displaying the total calorie count
        bottomBar = {
            TotalCaloriesBottomBar(totalCalories = trackingUiState.totalCalories)
        }
    ) { innerPadding ->
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
                // --- Date Selection Area ---
                // A read-only text field that triggers the DatePicker dialog when clicked
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimens.paddingLarge, vertical = AppTheme.dimens.paddingMedium)
                ) {
                    // Format the selected timestamp into a readable string
                    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    val dateString = Instant.ofEpochMilli(selectedDate)
                        .atZone(ZoneId.systemDefault())
                        .format(dateFormatter)

                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { }, // Read-only
                        readOnly = true,
                        label = { Text("Filter by Date") },
                        trailingIcon = {
                            Icon(Icons.Filled.DateRange, contentDescription = stringResource(R.string.select_date))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }, // Click handling on modifier
                        enabled = false, // Disable keyboard input
                        // Custom colors to make the disabled field look active/clickable
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )

                    // Invisible overlay to ensure clicks are captured over the entire text field area
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                // --- Date Picker Dialog ---
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                // Update ViewModel if a date was selected
                                datePickerState.selectedDateMillis?.let {
                                    viewModel.updateSelectedDate(it)
                                }
                                showDatePicker = false
                            }) {
                                Text(stringResource(R.string.ok))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                // --- Add Meal Dialog ---
                // Shows a searchable list of meals to add to the tracking log
                if (showAddMealDialog) {
                    AddTrackedMealDialog(
                        viewModel = viewModel,
                        mealList = allMeals,
                        onDismiss = { showAddMealDialog = false },
                        onMealSelected = { meal ->
                            viewModel.trackNewMeal(meal)
                            showAddMealDialog = false
                        }
                    )
                }

                // --- Main List of Tracked Meals ---
                TrackedMealList(
                    mealList = trackingUiState.mealList,
                    onMealClick = navigateToMealDetail,
                    onDeleteClick = { viewModel.removeTrackedMeal(it) },
                    emptyText = stringResource(R.string.no_tracked_meals_description),
                    modifier = Modifier
                )
            }
        }
    }
}

/**
 * Bottom bar composable that displays the total calories for the day.
 */
@Composable
fun TotalCaloriesBottomBar(totalCalories: Int) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = AppTheme.dimens.bottomBarShadowElevation,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimens.paddingMedium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.total_calories),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$totalCalories",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Custom Dialog composable for searching and selecting a meal to track.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTrackedMealDialog(
    mealList: List<Meal>,
    onDismiss: () -> Unit,
    onMealSelected: (Meal) -> Unit,
    viewModel: TrackingViewModel
) {
    val searchQuery by viewModel.searchQuery.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = { Text(
            text = stringResource(R.string.select_meal_track),
            style = MaterialTheme.typography.headlineSmall,
        ) },
        text = {
            Column {
                // Search Bar inside the dialog
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimens.paddingSmall),
                    placeholder = { Text(stringResource(R.string.search_bar_meal)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search_icon))
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                // Scrollable list of meals matching the search
                LazyColumn(
                    modifier = Modifier.height(AppTheme.dimens.dialogHeight)
                ) {
                    items(mealList) { meal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMealSelected(meal) }
                                .padding(vertical = AppTheme.dimens.paddingMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            AsyncImage(
                                model = meal.image,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(AppTheme.dimens.iconImageHeight)
                                    .width(AppTheme.dimens.iconImageHeight)
                                    .clip(MaterialTheme.shapes.small)
                                    .padding(end = AppTheme.dimens.paddingSmall),
                                contentScale = ContentScale.Crop
                            )
                            // Meal Name
                            Text(text = meal.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

/**
 * Displays the list of tracked meal entries.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackedMealList(
    mealList: List<TrackedMealEntry>,
    onMealClick: (Int) -> Unit,
    onDeleteClick: (TrackedMealEntry) -> Unit,
    emptyText: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (mealList.isEmpty()) {
            // Empty State
            Text(
                text = emptyText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(AppTheme.dimens.paddingLarge)
            )
        } else {
            // List Content
            LazyColumn(
                contentPadding = PaddingValues(bottom = AppTheme.dimens.listPaddingForFAB), // Space for FAB
                modifier = Modifier.padding(horizontal = AppTheme.dimens.paddingLarge)
            ) {
                // Use trackId as key for efficient recomposition
                items(items = mealList, key = { it.trackId }) { entry ->
                    TrackedInventoryItem(
                        entry = entry,
                        onDeleteClick = { onDeleteClick(entry) },
                        modifier = Modifier
                            .padding(bottom = AppTheme.dimens.paddingLarge)
                            .clickable { onMealClick(entry.meal.id) }
                    )
                }
            }
        }
    }
}

/**
 * A single row item in the tracking list.
 * Includes a delete button specific to the tracking entry.
 */
@Composable
fun TrackedInventoryItem(
    entry: TrackedMealEntry,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(AppTheme.dimens.cardElevation),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimens.listItemHeight) // Consistent height
        ) {
            // Image
            AsyncImage(
                model = entry.meal.image,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .width(AppTheme.dimens.listImageSize)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Text info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(AppTheme.dimens.paddingMedium),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = entry.meal.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${entry.meal.calories} cal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Delete Button
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.tracking_remove),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
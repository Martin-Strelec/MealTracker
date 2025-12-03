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

object TrackingDestination : NavigationDestination {
    override val route = "tracking"
    override val titleRes = R.string.tracking
    override val icon = Icons.Filled.DateRange
    override val showInDrawer = true
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackingScreen(
    navigateToMealDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val trackingUiState by viewModel.trackingUiState.collectAsState()
    val allMeals by viewModel.allMealsState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showAddMealDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMealDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_meal_tracked))
            }
        },
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimens.paddingLarge, vertical = AppTheme.dimens.paddingMedium)
                ) {
                    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                    val dateString = Instant.ofEpochMilli(selectedDate)
                        .atZone(ZoneId.systemDefault())
                        .format(dateFormatter)

                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Filter by Date") },
                        trailingIcon = {
                            Icon(Icons.Filled.DateRange, contentDescription = stringResource(R.string.select_date))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        enabled = false, // Disable direct text input, handle via Box click or modifier
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )

                    // Invisible overlay to capture clicks
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
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
                // Add Meal Dialog
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
                // Tracked List
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
                        // 3. Ensure Search bar contrasts slightly with the dialog background
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
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
            Text(
                text = emptyText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(AppTheme.dimens.paddingLarge)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = AppTheme.dimens.listPaddingForFAB), // Space for FAB
                modifier = Modifier.padding(horizontal = AppTheme.dimens.paddingLarge)
            ) {
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
            AsyncImage(
                model = entry.meal.image,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .width(AppTheme.dimens.listImageSize)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

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

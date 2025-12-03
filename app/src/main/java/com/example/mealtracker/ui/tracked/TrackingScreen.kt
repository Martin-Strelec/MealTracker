package com.example.mealtracker.ui.tracked

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.R
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.home.HomeBody
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
    val selectedDate by viewModel.selectedDate.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    Scaffold { innerPadding ->
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
                            Icon(Icons.Filled.DateRange, contentDescription = "Select Date")
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
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
                HomeBody(
                    mealList = trackingUiState.mealList,
                    onMealClick = navigateToMealDetail,
                    emptyText = stringResource(R.string.no_tracked_meals_description),
                    modifier = Modifier
                )
            }
        }
    }
}
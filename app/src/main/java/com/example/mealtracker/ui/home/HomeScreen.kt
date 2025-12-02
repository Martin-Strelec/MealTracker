package com.example.mealtracker.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.R
import com.example.mealtracker.data.Meal
import com.example.mealtracker.ui.AppViewModelProvider
import com.example.mealtracker.ui.meal.toStringDate
import com.example.mealtracker.ui.navigation.NavigationDestination
import com.example.mealtracker.ui.theme.AppTheme
import com.example.mealtracker.ui.theme.TWEEN_16
import com.example.mealtracker.ui.theme.TWEEN_24
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.toDuration

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.meals
    override val icon = Icons.Filled.Home
    override val showInDrawer = true
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAddMeal: () -> Unit,
    navigateToMealDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddMeal,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                Icon(Icons.Filled.Add, contentDescription = "Add Meal")
            }
        },
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
                 //Your main content, like a list of meals, will go here
                 //For now, let's put our search bar inside
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TWEEN_24),
                    placeholder = { Text("Search for a meal...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search Icon")
                    },
                    singleLine = true
                )
                HomeBody(
                    mealList = homeUiState.mealList,
                    onMealClick = navigateToMealDetail,
                    emptyText = stringResource(R.string.no_meals_description),
                    modifier = Modifier
                )

//                // You can replace this with your list of meals later
//                Greeting(
//                    name = "Content Area",
//                    modifier = Modifier.align(Alignment.Center)
//                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeBody(
    mealList: List<Meal>,
    onMealClick: (Int) -> Unit,
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
                modifier = Modifier.padding(TWEEN_24)
            )
        } else {
            MealList(
                mealList = mealList,
                onItemClick = { onMealClick(it.id) },
                contentPadding = PaddingValues(),
                modifier = Modifier.padding(TWEEN_24)
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealList(
    mealList: List<Meal>,
    onItemClick: (Meal) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = mealList, key = {it.id}) {item ->
            InventoryItem(
                item = item,
                modifier = Modifier
                    .padding(bottom = TWEEN_16)
                    .clickable { onItemClick(item) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InventoryItem(
    item: Meal,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(TWEEN_16),
            verticalArrangement = Arrangement.spacedBy(TWEEN_16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.calories.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = item.toStringDate(item.dateAdded),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    inputField: @Composable () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SearchBarDefaults.inputFieldShape,
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = SearchBarDefaults.TonalElevation,
    shadowElevation: Dp = SearchBarDefaults.ShadowElevation,
    windowInsets: WindowInsets = SearchBarDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit
) {

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    AppTheme {
        HomeBody(listOf(
            Meal(1, "Brambor", "S", "Super", 5, System.currentTimeMillis(), false , false),
            Meal(2, "Mrkev", "S", "Orange", 12, System.currentTimeMillis(),false , false),
            Meal(3, "Cibule", "S", "White", 15, System.currentTimeMillis(), false, false)
        ), onMealClick = {},
            emptyText = "No meals found")
    }
}


//@Preview(showBackground = true)
//@Composable
//fun MainPreview() {
//    AppTheme {
//        HomeScreen(navigateToAddMeal = {}, navigateToMealUpdate = {})
//    }
//}
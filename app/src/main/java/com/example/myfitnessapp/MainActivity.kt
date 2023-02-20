package com.example.myfitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.myfitnessapp.ui.theme.MyFitnessAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MyFitnessAppTheme {
                @OptIn(ExperimentalMaterial3Api::class)
                Scaffold { padding -> // We need to pass scaffold's inner padding to content. That's why we use Box.
                    Box(modifier = Modifier.padding(padding)) {
                        Navigation()
                    }
                }
            }
        }
    }
}


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController = navController)
        }
        //TODO: use that with an exercise item click listener
        composable(
            "details/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("exerciseId")?.let { exercise ->
//                ExerciseDetailsScreen(exercise = exercise)
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController) {
    val searchTextState = rememberSaveable { mutableStateOf("") }
    Column {
        SearchView(searchTextState)
        if (searchTextState.value.isNotEmpty()) {
            PagingListScreen(navController = navController, searchTextState = searchTextState)
        } else {
            //TODO: Some initial screen
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(state: MutableState<String>) {
    /**
     * For each keystroke, the state is changed, which means a new network call is initiated.
     * A debounce could be implemented either here, or in the ViewModel.
     */
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 18.sp),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != "") {
                IconButton(
                    onClick = {
                        state.value = ""
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}


@Composable
fun PagingListScreen(navController: NavController, searchTextState: MutableState<String>) {
    val viewModel = hiltViewModel<ExercisesViewModel>()
    val exercises = viewModel.exercises(searchTextState.value).collectAsLazyPagingItems()

    LazyColumn(
        content = {
            /**
             * An item header with the full muscle name cold be placed here. That would help in the case the user
             * searched for e.g.: "bic" and therefore the first result would be "bicep" and the result for "bicep" would
             * be listed in itemsIndexed.
            item {
            Text(
            text = "Complete muscle name",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge
            )
            }
             */
            itemsIndexed(
                items = exercises,
                key = { _: Int, article ->
                    article.id
                },
                itemContent = { _, muscles ->
                    Text(
                        modifier = Modifier.height(75.dp),
                        text = muscles?.name ?: "",
                    )
                    Text(
                        modifier = Modifier.height(75.dp),
                        text = muscles?.description ?: ""
                    )
                    Divider()
                }
            )

            when (val refreshState = exercises.loadState.refresh) {
                is LoadState.Error -> {
                    //TODO Error Item. refreshState.error to get error message
                }
                is LoadState.Loading -> { // Loading UI
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxSize(),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = "Loading..."
                            )

                            CircularProgressIndicator(color = Color.Black)
                        }
                    }
                }
                else -> {}
            }

            when (val appendState = exercises.loadState.append) {
                is LoadState.Error -> {
                    //TODO Pagination Error Item. appendState.error to get error message
                }
                is LoadState.Loading -> { // Pagination Loading UI
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "Loading next page...")

                            CircularProgressIndicator(color = Color.Black)
                        }
                    }
                }
                else -> {}
            }

        })
}


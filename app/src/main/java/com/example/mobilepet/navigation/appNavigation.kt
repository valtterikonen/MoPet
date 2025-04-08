package com.example.mobilepet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.R
import com.example.mobilepet.screens.ExerciseScreen
import com.example.mobilepet.screens.HomeScreen
import com.example.mobilepet.screens.SettingsScreen
import kotlinx.coroutines.launch


@Composable
fun AppNavigation () {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("exercise") {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    ExerciseScreen(navController)
                }
            }
        }

        composable("home") {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(navController, snackbarHostState)
                }
            }
        }

        composable("settings") {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    SettingsScreen(navController)
                }
            }
        }

    }
}
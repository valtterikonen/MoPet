package com.example.mobilepet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.mobilepet.screens.ExerciseScreen
import com.example.mobilepet.screens.HomeScreen
import com.example.mobilepet.screens.SettingsScreen

@Composable
fun AppNavigation(isLightTheme: MutableState<Boolean>) { // Pass the theme state
    val navController = rememberNavController()
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
                    SettingsScreen(navController, isLightTheme) // Pass isLightTheme to SettingsScreen
                }
            }
        }
    }
}
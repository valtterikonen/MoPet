package com.example.mobilepet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.mobilepet.screens.ExerciseScreen
import com.example.mobilepet.screens.HomeScreen
import com.example.mobilepet.screens.PetPicture
import com.example.mobilepet.screens.SettingsScreen

@Composable
fun AppNavigation(isLightTheme: MutableState<Boolean>) {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    navController.currentBackStackEntryAsState().value?.destination?.route

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
                    ExerciseScreen()
                }
            }
        }

        composable("home") {
            Scaffold(
                snackbarHost = { SnackbarHost(snackBarHostState) },
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(navController, snackBarHostState)
                }
            }
        }

        composable("settings") {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    SettingsScreen(navController, isLightTheme)
                }
            }
        }

        composable("picture") {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    PetPicture(navController)
                }
            }
        }

        composable("gallery") {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Text("gallery")
                }
            }
        }

    }
}
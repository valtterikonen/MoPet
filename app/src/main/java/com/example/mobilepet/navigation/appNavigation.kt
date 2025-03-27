package com.example.mobilepet.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.screens.ExerciseScreen
import com.example.mobilepet.screens.HomeScreen
import com.example.mobilepet.screens.SettingsScreen


@Composable
fun AppNavigation () {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

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
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                HomeScreen(navController)
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
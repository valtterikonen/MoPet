package com.example.mobilepet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar{
        NavigationBarItem(
            onClick = { navController.navigate("exercise") },
            icon = { Icon(Icons.Filled.Face, contentDescription = "Exercise") },
            label = { Text("Exercise") },
            selected = navController.currentDestination?.route == "exercise"
        )

        NavigationBarItem(
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "home"
        )

        NavigationBarItem(
            onClick = { navController.navigate("settings") },
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = navController.currentDestination?.route == "settings"
        )
    }
}
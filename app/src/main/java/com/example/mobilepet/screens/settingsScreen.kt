package com.example.mobilepet.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController, startDestination = "settings") {
                composable("settings") { SettingsScreen() }
                composable("home") { Text("Home Screen", fontSize = 24.sp) }
                composable("exercise") { Text("Exercise Screen", fontSize = 24.sp) }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    var soundEnabled by remember { mutableStateOf(true) }
    var isLightTheme by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Äänet", fontSize = 18.sp, modifier = Modifier.weight(1f))
            Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = isLightTheme, onCheckedChange = { isLightTheme = it })
            Text("Light", fontSize = 18.sp, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            label = { Text("Exercise") },
            selected = false,
            onClick = { navController.navigate("exercise") },
            icon = {}
        )
        NavigationBarItem(
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") },
            icon = {}
        )
        NavigationBarItem(
            label = { Text("Settings") },
            selected = false,
            onClick = { navController.navigate("settings") },
            icon = {}
        )
    }
}

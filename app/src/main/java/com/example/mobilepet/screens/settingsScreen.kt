package com.example.mobilepet.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

@Composable
fun SettingsScreen(navController: NavController) {
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

@Preview
@Composable
fun PreviewSettingsScreen() {
    val navController = rememberNavController()
    SettingsScreen(navController)
}
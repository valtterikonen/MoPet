package com.example.mobilepet.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.navigation.BottomNavigationBar
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun DynamicTheme(content: @Composable (MutableState<Boolean>) -> Unit) {
    val isLightTheme = rememberSaveable { mutableStateOf(true) }

    val colors = if (isLightTheme.value) lightColorScheme() else darkColorScheme()

    MaterialTheme(colorScheme = colors) {
        content(isLightTheme)
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    DynamicTheme { isLightTheme ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                var soundEnabled by rememberSaveable { mutableStateOf(true) }

                SettingItem("Äänet", soundEnabled, "ON", "OFF") { soundEnabled = it }
                Spacer(modifier = Modifier.height(16.dp))
                SettingItem("Teema", isLightTheme.value, "LIGHT", "DARK") { newTheme ->
                    isLightTheme.value = newTheme
                }
            }
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun SettingItem(label: String, state: Boolean, onText: String, offText: String, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 18.sp)

        Button(
            onClick = { onToggle(!state) },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state) Color.Green else Color.Gray
            ),
            modifier = Modifier.width(100.dp)
        ) {
            Text(if (state) onText else offText, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}

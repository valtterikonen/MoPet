package com.example.mobilepet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.navigation.AppNavigation
import com.example.mobilepet.ui.theme.MobilePetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isLightTheme = rememberSaveable { mutableStateOf(true) }

            MobilePetTheme(isLightTheme) {
                AppNavigation(isLightTheme)
            }
        }
    }
}
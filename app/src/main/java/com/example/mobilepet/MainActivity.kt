package com.example.mobilepet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mobilepet.navigation.AppNavigation
import com.example.mobilepet.ui.theme.MobilePetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobilePetTheme {
                AppNavigation()
            }
        }
    }
}
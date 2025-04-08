package com.example.mobilepet.screens

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.navigation.BottomNavigationBar
import earth.worldwind.WorldWindow
import earth.worldwind.layer.BlueMarbleLayer


@Composable
fun ExerciseScreen(navController: NavController) {
    Text("Exercise Screen")

    Scaffold (
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ){ innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    )
            ){
                Text(
                    text = "Status Bar",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            //Tästä alkaa kartan ihmettely
            GlobeComposable()
        }
    }
}

@Composable
fun GlobeComposable() {
    val context = LocalContext.current

    AndroidView(factory = { createWorldWindow(context) })
}

fun createWorldWindow(context: Context): WorldWindow {
    val worldWindow = WorldWindow(context)
    worldWindow.engine.layers.addLayer(BlueMarbleLayer()) // Add a basic globe layer
    return worldWindow
}


@Preview
@Composable
fun ExerciseScreenPreview() {
    val navController = rememberNavController()
    ExerciseScreen(navController)
}

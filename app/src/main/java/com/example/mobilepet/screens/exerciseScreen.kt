package com.example.mobilepet.screens

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.navigation.BottomNavigationBar
import earth.worldwind.WorldWindow
import earth.worldwind.geom.AltitudeMode
import earth.worldwind.geom.Position
import earth.worldwind.layer.BlueMarbleLayer
import earth.worldwind.layer.RenderableLayer
import earth.worldwind.shape.Path
import earth.worldwind.shape.PathType
import kotlin.random.Random


@Composable
fun ExerciseScreen(navController: NavController) {
    val context = LocalContext.current
    val worldWindow = remember { createWorldWindow(context) }
    var showTrajectory by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTrajectory = !showTrajectory },
                modifier = Modifier
                    .padding(16.dp)
            ) {

            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.medium
                    )
            ) {
                Text(
                    text = "Status Bar",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // Globe rendering
            AndroidView(factory = { worldWindow })

            // Add trajectory layer if toggled
            if (showTrajectory) {
                addTrajectoryLayer(worldWindow)
            }
        }
    }
}

// Luo WorldWindow-objektin
fun createWorldWindow(context: Context): WorldWindow {
    val worldWindow = WorldWindow(context)
    worldWindow.engine.layers.addLayer(BlueMarbleLayer()) // Add a basic globe layer
    return worldWindow
}

//Generoi kolme satunnaista sijaintia
fun addTrajectoryLayer(worldWindow: WorldWindow) {
    val layer = RenderableLayer()
    val positions = generateRandomPositions(3)
    val path = Path(positions).apply {
        isFollowTerrain = true
        pathType = PathType.LINEAR
        altitudeMode = AltitudeMode.CLAMP_TO_GROUND
    }
    layer.addRenderable(path)
    worldWindow.engine.layers.addLayer(layer)
}

//Generoi satunnaisia sijainteja
fun generateRandomPositions(count: Int): List<Position> {
    return List(count) {
        Position.fromDegrees(
            Random.nextDouble(-90.0, 90.0), // Random latitude
            Random.nextDouble(-180.0, 180.0), // Random longitude
            0.0 // Altitude
        )
    }
}

@Preview
@Composable
fun ExerciseScreenPreview() {
    val navController = rememberNavController()
    ExerciseScreen(navController)
}
package com.example.mobilepet.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import earth.worldwind.geom.AltitudeMode
import earth.worldwind.geom.Position
import earth.worldwind.layer.BlueMarbleLayer
import earth.worldwind.layer.RenderableLayer
import earth.worldwind.shape.Path
import earth.worldwind.shape.PathType
import earth.worldwind.shape.Placemark
import earth.worldwind.render.Color
import earth.worldwind.shape.PlacemarkAttributes
import kotlinx.coroutines.*
import kotlin.times


@Composable
fun ExerciseScreen(navController: NavController) {
    val context = LocalContext.current
    val worldWindow = remember { createWorldWindow(context) }
    var showTrajectory by remember { mutableStateOf(false) }
    var currentSteps by remember { mutableStateOf(0) }
    val goalSteps = 10000 // Example goal steps
    val positions = remember { generateRandomPositions(3) }
    val marker = remember { createMarker(positions.first()) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    currentSteps = event.values[0].toInt() // Update steps
                    updateMarkerPosition(marker, positions, currentSteps, goalSteps)
                    worldWindow.requestRedraw() // Request redraw to update the marker position
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(stepListener)
        }
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showTrajectory = !showTrajectory
                    if (showTrajectory)  {
                        addMarker(worldWindow, marker)
                    }
                          },
                modifier = Modifier
                    .padding(16.dp)
            ) {
            Text(
                text = "Exercise",
                color = androidx.compose.ui.graphics.Color.White
            )
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Steps: $currentSteps / $goalSteps",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    LinearProgressIndicator(
                        progress = currentSteps / goalSteps.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            // Globe rendering
            AndroidView(factory = { worldWindow })

            // Add trajectory layer if toggled
            if (showTrajectory) {
                addTrajectoryLayer(worldWindow, positions)
                addMarker(worldWindow, marker)
            }
        }
    }
}

// Luo WorldWindow-objektin
fun createWorldWindow(context: Context): WorldWindow {
    val worldWindow = WorldWindow(context)
    worldWindow.engine.layers.addLayer(BlueMarbleLayer()) // Lisää taustakerroksen
    return worldWindow
}

//Lisää reittikerroksen
fun addTrajectoryLayer(worldWindow: WorldWindow, positions: List<Position>) {
    val layer = RenderableLayer() // Luo uuden kerroksen
    val path = Path(positions).apply {
        isFollowTerrain = true
        pathType = PathType.LINEAR
        altitudeMode = AltitudeMode.CLAMP_TO_GROUND
    }
    layer.addRenderable(path)
    worldWindow.engine.layers.addLayer(layer)

    val startingPoint = positions.first() //luodaan ensimmäinen sijainti
    val camera = worldWindow.engine.camera // Haetaan kamera

    // Animoitu kamera liikkumaan satunnaiseen sijaintiin
    CoroutineScope(Dispatchers.Main).launch {
        val startLatitude = camera.position.latitude
        val startLongitude = camera.position.longitude
        val startAltitude = camera.position.altitude
        val endLatitude = startingPoint.latitude
        val endLongitude = startingPoint.longitude
        val endAltitude = 10000000.0 // Oletettu loppukorkeus
        val duration = 2000L // Animaation kestoaika (2 sekuntia)
        val steps = 60 // Animaation vaiheet
        val delayTime = duration / steps

        for (i in 1..steps) {
            val fraction = i / steps.toFloat()
            camera.position.latitude = startLatitude + (endLatitude - startLatitude) * fraction
            camera.position.longitude = startLongitude + (endLongitude - startLongitude) * fraction
            camera.position.altitude = startAltitude + (endAltitude - startAltitude) * fraction
            worldWindow.requestRedraw()
            delay(delayTime)
        }
    }
}

fun createMarker(position: Position): Placemark {
    val attributes = PlacemarkAttributes().apply {
        imageSource = null // No image, just a box

    }

    return Placemark(position).apply {
        this.attributes = attributes
        altitudeMode = AltitudeMode.CLAMP_TO_GROUND
    }
}

fun updateMarkerPosition(marker: Placemark, positions: List<Position>, currentSteps: Int, goalSteps: Int) {
    val progress = (currentSteps.toFloat() / goalSteps).coerceIn(0f, 1f)
    val segmentCount = positions.size - 1
    val segmentIndex = (progress * segmentCount).toInt().coerceAtMost(segmentCount - 1)
    val segmentProgress = (progress * segmentCount) - segmentIndex

    val start = positions[segmentIndex]
    val end = positions[segmentIndex + 1]

    marker.position.latitude = start.latitude + (end.latitude - start.latitude) * segmentProgress
    marker.position.longitude = start.longitude + (end.longitude - start.longitude) * segmentProgress
    marker.position.altitude = start.altitude + (end.altitude - start.altitude) * segmentProgress
}

fun addMarker(worldWindow: WorldWindow, marker: Placemark) {
    val layer = RenderableLayer()
    layer.addRenderable(marker)
    worldWindow.engine.layers.addLayer(layer)
}

//Asettaa satunnaiset sijainnit listan paikkojen väliltä
fun generateRandomPositions(count: Int): List<Position> {
    val countryCoordinates = listOf(
        Position.fromDegrees(60.0, 24.0, 0.0), // Helsinki, Finland
        Position.fromDegrees(48.8566, 2.3522, 0.0), // Paris, France
        Position.fromDegrees(51.5074, -0.1278, 0.0), // London, UK
        Position.fromDegrees(40.7128, -74.0060, 0.0), // New York, USA
        Position.fromDegrees(35.6895, 139.6917, 0.0), // Tokyo, Japan
        Position.fromDegrees(-33.8688, 151.2093, 0.0), // Sydney, Australia
        Position.fromDegrees(-23.5505, -46.6333, 0.0), // São Paulo, Brazil
        Position.fromDegrees(55.7558, 37.6173, 0.0), // Moscow, Russia
        Position.fromDegrees(39.9042, 116.4074, 0.0), // Beijing, China
        Position.fromDegrees(1.3521, 103.8198, 0.0) // Singapore

    )
    return List(count) {
        countryCoordinates.random() // Satunnainen sijainti listasta
    }
}

@Preview
@Composable
fun ExerciseScreenPreview() {
    val navController = rememberNavController()
    ExerciseScreen(navController)
}

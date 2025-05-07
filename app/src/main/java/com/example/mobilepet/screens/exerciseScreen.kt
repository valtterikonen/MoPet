package com.example.mobilepet.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilepet.models.PetModel
import earth.worldwind.WorldWindow
import earth.worldwind.geom.AltitudeMode
import earth.worldwind.geom.Angle
import earth.worldwind.geom.Camera
import earth.worldwind.geom.Position
import earth.worldwind.layer.BlueMarbleLayer
import earth.worldwind.layer.RenderableLayer
import earth.worldwind.shape.Path
import earth.worldwind.shape.PathType
import earth.worldwind.shape.Placemark
import earth.worldwind.shape.PlacemarkAttributes
import kotlinx.coroutines.*
import kotlin.math.sqrt

@Composable
fun ExerciseScreen() {
    val context = LocalContext.current
    val petModel: PetModel = viewModel()
    val worldWindow = remember { createWorldWindow(context) }

    var showTrajectory by remember { mutableStateOf(false) }
    var isExercising by remember { mutableStateOf(false) }
    val currentSteps = remember { mutableIntStateOf(0) }
    var lastExerciseStep by remember { mutableIntStateOf(0) }

    val goalSteps = 1000
    val positions = remember { generateRandomPositions(3) }
    val marker = remember { createMarker(positions.first()) }

    // Sennsorin kuuntelija askelien laskemiseen
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        var lastMagnitude = 0f
        val stepThreshold = 1f
        var lastStepTime = 0L

        // SensorEventListener askelien laskemiseen
        val stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null || !isExercising) return

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val magnitude = sqrt(x * x + y * y + z * z)
                val delta = magnitude - lastMagnitude
                lastMagnitude = magnitude

                val currentTime = System.currentTimeMillis()
                if (delta > stepThreshold && currentTime - lastStepTime > 300) {
                    lastStepTime = currentTime

                    currentSteps.intValue++
                    Log.d("EXERCISE", "Askel: ${currentSteps.intValue}")

                    updateMarkerPosition(marker, positions, currentSteps.intValue, goalSteps, worldWindow)
                    worldWindow.requestRedraw()

                    if (currentSteps.intValue >= lastExerciseStep + 50) {
                        petModel.exercisePet()
                        lastExerciseStep = currentSteps.intValue
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Rekisteröidään sensorin kuuntelija
        if (accelerometer != null) {
            sensorManager.registerListener(stepListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }

        // Poistetaan kuuntelija kun komponentti tuhotaan
        onDispose {
            sensorManager.unregisterListener(stepListener)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isExercising = !isExercising
                    showTrajectory = !showTrajectory
                    if (showTrajectory) {
                        addMarker(worldWindow, marker)
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = if (isExercising) "Stop" else "Exercise")
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
                    .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Steps: ${currentSteps.intValue} / $goalSteps",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    LinearProgressIndicator(
                        progress = { (currentSteps.intValue / goalSteps.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
            // Näytetään WorldWindow kerros
            AndroidView(factory = { worldWindow })

            // Lisätään reittikerros ja merkki
            if (showTrajectory) {
                addTrajectoryLayer(worldWindow, positions)
                addMarker(worldWindow, marker)
            }
        }
    }
}

// Luodaan WorldWindow-olio
fun createWorldWindow(context: Context): WorldWindow {
    val worldWindow = WorldWindow(context)
    worldWindow.engine.layers.addLayer(BlueMarbleLayer())
    return worldWindow
}

// Lisätään reittikerros
fun addTrajectoryLayer(worldWindow: WorldWindow, positions: List<Position>) {
    val layer = RenderableLayer()
    val path = Path(positions).apply {
        isFollowTerrain = true
        pathType = PathType.LINEAR
        altitudeMode = AltitudeMode.CLAMP_TO_GROUND
    }
    layer.addRenderable(path)
    worldWindow.engine.layers.addLayer(layer)

    val camera = worldWindow.engine.camera
    val start = camera.position
    val end = positions.first()
    val duration = 2000L
    val steps = 60
    val delayTime = duration / steps

    CoroutineScope(Dispatchers.Main).launch {
        for (i in 1..steps) {
            val fraction = i / steps.toFloat()
            camera.position.latitude = start.latitude + (end.latitude - start.latitude) * fraction
            camera.position.longitude = start.longitude + (end.longitude - start.longitude) * fraction
            camera.position.altitude = start.altitude + (10_000_000.0 - start.altitude) * fraction
            worldWindow.requestRedraw()
            delay(delayTime)
        }
    }
}

// Luodaan merkki
fun createMarker(position: Position): Placemark {
    val attributes = PlacemarkAttributes().apply {
        imageSource = null
        imageScale = 6.0
    }
    return Placemark(position).apply {
        this.attributes = attributes
        altitudeMode = AltitudeMode.CLAMP_TO_GROUND
    }
}

// Päivitetään merkin sijainti
fun updateMarkerPosition(
    marker: Placemark,
    positions: List<Position>,
    currentSteps: Int,
    goalSteps: Int,
    worldWindow: WorldWindow
) {
    val progress = calculateProgress(currentSteps, goalSteps)
    val (start, end, segmentProgress) = calculateSegment(positions, progress)

    updateMarkerPosition(marker, start, end, segmentProgress)
    updateCameraPosition(worldWindow, marker)
    worldWindow.requestRedraw()
}

// Lasketaan edistymisprosentti
private fun calculateProgress(currentSteps: Int, goalSteps: Int): Float {
    return (currentSteps.toFloat() / goalSteps).coerceIn(0f, 1f)
}

// Lasketaan segmentti
private fun calculateSegment(
    positions: List<Position>,
    progress: Float
): Triple<Position, Position, Float> {
    val segmentCount = positions.size - 1
    val segmentIndex = (progress * segmentCount).toInt().coerceAtMost(segmentCount - 1)
    val segmentProgress = (progress * segmentCount) - segmentIndex
    return Triple(positions[segmentIndex], positions[segmentIndex + 1], segmentProgress)
}

// Päivitetään merkin sijainti
private fun updateMarkerPosition(
    marker: Placemark,
    start: Position,
    end: Position,
    segmentProgress: Float
) {
    marker.position.latitude = interpolate(start.latitude.inDegrees, end.latitude.inDegrees, segmentProgress).let {
        Angle.fromDegrees(it)
    }
    marker.position.longitude = interpolate(start.longitude.inDegrees, end.longitude.inDegrees, segmentProgress).let {
        Angle.fromDegrees(it)
    }
    marker.position.altitude = interpolate(start.altitude, end.altitude, segmentProgress)
}

// Päivitetään kameran sijainti aloitus- ja lopetussijainnin mukaan
private fun updateCameraPosition(worldWindow: WorldWindow, marker: Placemark) {
    updateCameraPosition(
        worldWindow.engine.camera,
        marker.position.latitude,
        marker.position.longitude,
        marker.position.altitude + 10_000_000.0
    )
}

// Päivitetään kameran sijainti liikkeen aikana
private fun updateCameraPosition(camera: Camera, latitude: Angle, longitude: Angle, altitude: Double) {
    camera.position.latitude = latitude
    camera.position.longitude = longitude
    camera.position.altitude = altitude
}

// Interpoloi kahden arvon välillä
private fun interpolate(start: Double, end: Double, fraction: Float): Double {
    return start + (end - start) * fraction
}

// Lisätään merkki kartalle
fun addMarker(worldWindow: WorldWindow, marker: Placemark) {
    val layer = RenderableLayer()
    layer.addRenderable(marker)
    worldWindow.engine.layers.addLayer(layer)
}

// Generoidaan satunnaisia sijainteja
fun generateRandomPositions(count: Int): List<Position> {
    val coords = listOf(
        Position.fromDegrees(60.0, 24.0, 0.0),
        Position.fromDegrees(48.8566, 2.3522, 0.0),
        Position.fromDegrees(51.5074, -0.1278, 0.0),
        Position.fromDegrees(40.7128, -74.0060, 0.0),
        Position.fromDegrees(35.6895, 139.6917, 0.0),
        Position.fromDegrees(-33.8688, 151.2093, 0.0),
        Position.fromDegrees(-23.5505, -46.6333, 0.0),
        Position.fromDegrees(55.7558, 37.6173, 0.0),
        Position.fromDegrees(39.9042, 116.4074, 0.0),
        Position.fromDegrees(1.3521, 103.8198, 0.0)
    )
    return List(count) { coords.random() }
}
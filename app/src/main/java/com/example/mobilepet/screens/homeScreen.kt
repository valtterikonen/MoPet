package com.example.mobilepet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilepet.R
import com.example.mobilepet.components.CustomFloatingActionButton
import com.example.mobilepet.models.PetAnimations.feedAnimation
import com.example.mobilepet.models.PetModel
import com.example.mobilepet.models.StatusBarsColumn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, snackbarHostState: SnackbarHostState) {
    val petModel: PetModel = viewModel()

    var showDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var petType by remember { mutableStateOf("") }
    val animate = remember { mutableStateOf(false) }
    var animateFeed by remember { mutableStateOf(false) }
    var animateFlip by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petModel.pet) {
        showDialog = petModel.pet == null
    }

    // REST TIMER
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val interactionModifier = Modifier.pointerInput(Unit) {
        while (true) {
            awaitPointerEventScope {
                awaitPointerEvent()
                lastInteractionTime = System.currentTimeMillis()
            }
        }
    }
    LaunchedEffect(lastInteractionTime) {
        delay(120_000)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInteractionTime > 120_000) {
            petModel.restPet()
            lastInteractionTime = System.currentTimeMillis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(interactionModifier)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusBarsColumn(
            mood = petModel.pet?.mood ?: 0,
            energy = petModel.pet?.energy ?: 0,
            hunger = petModel.pet?.hunger ?: 0
        )
        Box(modifier = Modifier.fillMaxSize()) {
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Create Pet") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = petName,
                                onValueChange = { petName = it },
                                label = { Text("Name") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Type")
                            Row {
                                listOf("Dog", "Cat", "Hedgehog").forEach { type ->
                                    val isSelected = petType == type
                                    Button(
                                        onClick = { petType = type },
                                        modifier = Modifier.weight(1f).padding(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) {
                                        Text(type)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (petName.isNotBlank() && petType.isNotBlank()) {
                                petModel.createPet(petName, petType)
                                showDialog = false
                            }
                        }) {
                            Text("Create")
                        }
                    }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                if (petModel.pet != null) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = petModel.pet!!.name,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            OutlinedButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                Text("Execute ${petModel.pet!!.name}")
                            }
                            if (showDeleteDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDeleteDialog = false },
                                    title = { Text("Execute Pet") },
                                    text = { Text("Confirm executing your pet ${petModel.pet!!.name}?") },
                                    confirmButton = {
                                        Button(onClick = {
                                            petModel.resetPet()
                                            showDeleteDialog = false
                                        }) {
                                            Text("Execute \uD83D\uDE22\uD83D\uDC94")
                                        }
                                    },
                                    dismissButton = {
                                        OutlinedButton(onClick = { showDeleteDialog = false }) {
                                            Text("Cancel \uD83D\uDE42")
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (petModel.pet != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    CustomFloatingActionButton(
                        snackbarHostState = snackbarHostState,
                        onFeedSuccess = {
                            animate.value = true
                            coroutineScope.launch {
                                delay(1000)
                                animate.value = false
                            }
                        },
                        triggerAnimation = {
                            animateFeed = true
                            coroutineScope.launch {
                                delay(1000)
                                animateFeed = false
                            }
                        }
                    )
                }
                if (petModel.pet != null && petModel.pet?.type == "Dog") {
                    feedAnimation(shouldAnimate = animateFeed) { animateModifier ->
                        Image(
                            painter = painterResource(id = R.drawable.dog_crop),
                            contentDescription = "Dog",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .graphicsLayer(
                                    translationX = 0f,
                                    translationY = 0f,
                                    scaleX = 1f,
                                    scaleY = 1f
                                )
                                .then(animateModifier),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else if (petModel.pet != null && petModel.pet?.type == "Cat") {
                    feedAnimation(shouldAnimate = animateFeed) { animateModifier ->
                        Image(
                            painter = painterResource(id = R.drawable.kissa_crop),
                            contentDescription = "Cat",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .graphicsLayer(
                                    translationX = 0f,
                                    translationY = 0f,
                                    scaleX = 1f,
                                    scaleY = 1f
                                )
                                .then(animateModifier),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else if (petModel.pet != null && petModel.pet?.type == "Hedgehog") {
                    feedAnimation(shouldAnimate = animateFeed) { animateModifier ->
                        Image(
                            painter = painterResource(id = R.drawable.siili_crop),
                            contentDescription = "Hedgehog",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .graphicsLayer(
                                    translationX = 0f,
                                    translationY = 0f,
                                    scaleX = 1f,
                                    scaleY = 1f
                                )
                                .then(animateModifier),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

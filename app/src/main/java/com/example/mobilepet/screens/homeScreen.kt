package com.example.mobilepet.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mobilepet.R
import com.example.mobilepet.components.CustomFloatingActionButton
import com.example.mobilepet.models.PetAnimations.FeedAnimation
import com.example.mobilepet.models.PetAnimations.IdleAnimation
import com.example.mobilepet.models.PetModel
import com.example.mobilepet.models.StatusBarsColumn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, snackBarHostState: SnackbarHostState) {
    val petModel: PetModel = viewModel()

    var showDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var petType by remember { mutableStateOf("") }
    val animate = remember { mutableStateOf(false) }
    var animateFeed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petModel.pet) {
        showDialog = petModel.pet == null
    }

    // Lepoajastin, jonka jälkeen PetModelin restPet() aktivoituu -->
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastInteractionTime) {
        delay(120_000)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInteractionTime > 120_000) {
            petModel.restPet()
            lastInteractionTime = System.currentTimeMillis()
        }
    }


    //Lemmikin luomiseen käytettävä popup-ikkuna
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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


            //Lemmikin renderöinti
                if (petModel.pet != null) {
                        StatusBarsColumn(
                            mood = petModel.pet?.mood ?: 0,
                            energy = petModel.pet?.energy ?: 0,
                            hunger = petModel.pet?.hunger ?: 0
                        )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-22).dp)
                        ) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = petModel.pet!!.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            HorizontalDivider(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {

                            // Nappi lemmikin deletoimiseen
                            IconButton(onClick = {
                                showDeleteDialog = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.skull),
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .shadow(4.dp, shape = MaterialTheme.shapes.small),
                                    tint = Color.White
                                )
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
                                        OutlinedButton(onClick = {
                                            showDeleteDialog = false
                                        }) {
                                            Text("Cancel \uD83D\uDE42")
                                        }
                                    }
                                )
                            }

                            // Navigointi-nappi kameraruudulle
                            IconButton(onClick = {
                                navController.navigate("picture")
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camera),
                                    contentDescription = "Camera",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .shadow(4.dp, shape = MaterialTheme.shapes.small),
                                    tint = Color.White
                                )
                            }
                        }

                        // Lemmikin renderöinti perustuen käyttäjän valitaan
                        if (petModel.pet != null && petModel.pet?.type == "Dog") {
                            FeedAnimation(shouldAnimate = animateFeed) { animateModifier ->
                                IdleAnimation { idleModifier ->
                                    Image(
                                        painter = painterResource(id = R.drawable.dog_crop),
                                        contentDescription = "Dog",
                                        modifier = Modifier.then(idleModifier)
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
                        } else if (petModel.pet != null && petModel.pet?.type == "Cat") {
                            FeedAnimation(shouldAnimate = animateFeed) { animateModifier ->
                                IdleAnimation { idleModifier ->
                                    Image(
                                        painter = painterResource(id = R.drawable.kissa_crop),
                                        contentDescription = "Cat",
                                        modifier = Modifier.then(idleModifier)
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
                        } else if (petModel.pet != null && petModel.pet?.type == "Hedgehog") {
                            FeedAnimation(shouldAnimate = animateFeed) { animateModifier ->
                                IdleAnimation { idleModifier ->
                                    Image(
                                        painter = painterResource(id = R.drawable.siili_crop),
                                        contentDescription = "Hedgehog",
                                        modifier = Modifier.then(idleModifier)
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
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                        ) {

                            //Ruokinta-nappi
                            CustomFloatingActionButton(
                                snackBarHostState = snackBarHostState,
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
                    }
                }
    }
}
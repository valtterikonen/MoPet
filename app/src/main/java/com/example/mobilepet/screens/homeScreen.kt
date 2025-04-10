package com.example.mobilepet.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilepet.R
import com.example.mobilepet.models.PetModel
import com.example.mobilepet.models.StatusBarsColumn
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobilepet.components.CustomFloatingActionButton
import com.example.mobilepet.models.PetAnimations.feedAnimation
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, snackbarHostState: SnackbarHostState) {
    val petModel: PetModel = viewModel()

    var showDialog by remember { mutableStateOf(false) }
    var petName by remember { mutableStateOf("") }
    var petType by remember { mutableStateOf("") }
    val animate = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var animateFeed by remember { mutableStateOf(false) }


    LaunchedEffect(petModel.pet) {
        showDialog = petModel.pet == null
    }

    // PET REST TIMER //
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
        delay(60_000)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInteractionTime > 60_000) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.medium
                )
        ) {

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Luo lemmikki") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = petName,
                                onValueChange = { petName = it },
                                label = { Text("Lemmikin nimi") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Valitse tyyppi")
                            Row {
                                listOf("Dog", "Cat", "Turtle").forEach { type ->
                                    val isSelected = petType == type
                                    Button(
                                        onClick = { petType = type },
                                        modifier = Modifier.padding(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
                            Text("Luo")
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
                                petModel.resetPet()
                                showDialog = true
                            }) {
                                Text("Delete Pet")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        if (petModel.pet != null) {
                            feedAnimation(shouldAnimate = animateFeed) { animatedModifier ->
                                Image(
                                    painter = painterResource(id = R.drawable.kissa_crop),
                                    contentDescription = "Pet Image",
                                    modifier = animatedModifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                    }
                }
            }

            if (petModel.pet != null) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
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
                        }
                    )
                }
            }
        }
    }
}
package com.example.mobilepet.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobilepet.models.FeedResult
import com.example.mobilepet.models.PetModel
import kotlinx.coroutines.launch

@Composable
fun CustomFloatingActionButton(
    snackbarHostState: SnackbarHostState,
    onFeedSuccess: () -> Unit
) {
    val coroutine = rememberCoroutineScope()
    val petModel: PetModel = viewModel()

    FloatingActionButton(
        onClick = {

            val result = petModel.feedPet()

            coroutine.launch {
                when (result) {
                    is FeedResult.Success -> {
                        onFeedSuccess()
                        snackbarHostState.showSnackbar("Pet fed!")
                    }
                    is FeedResult.TooEarly -> {
                        val secondsLeft = (result.remainingTimeMs / 1000).coerceAtLeast(1)
                        snackbarHostState.showSnackbar("Too soon! Try again in $secondsLeft s")
                    }
                }
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Feed")
    }
}

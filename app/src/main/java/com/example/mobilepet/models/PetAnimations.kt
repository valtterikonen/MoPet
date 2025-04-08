package com.example.mobilepet.models

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


object PetAnimations {

    @Composable
    fun feedAnimation(shouldAnimate: Boolean): Modifier {
        val offsetY by animateDpAsState(
            targetValue = if (shouldAnimate) (-20).dp else 0.dp,
            animationSpec = tween(durationMillis = 300)
        )
        return Modifier.offset(y = offsetY)
    }


    @Composable
    fun flipWithJumpAnimation(shouldAnimate: Boolean): Modifier {

        val rotationProgress = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(shouldAnimate) {
            if (shouldAnimate) {
                rotationProgress.snapTo(0f)
                rotationProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }

        val rotationX = 360f * rotationProgress.value
        val rotationY = 360f * rotationProgress.value
        val offsetY = (-40 * kotlin.math.sin(Math.PI * rotationProgress.value)).dp

        val density = LocalDensity.current.density

        return Modifier
            .graphicsLayer {
                this.rotationX = rotationX
                this.rotationY = rotationY
                cameraDistance = 16f * density
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
            }
            .offset(y = offsetY)
    }



}

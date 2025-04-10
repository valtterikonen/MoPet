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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


object PetAnimations {

    @Composable
    fun feedAnimation(shouldAnimate: Boolean, content: @Composable (Modifier) -> Unit) {
        val offsetY = remember { Animatable(0f) }
        val offsetX = remember { Animatable(0f) }

        LaunchedEffect(shouldAnimate) {
            if (shouldAnimate) {
                repeat(3) {
                    offsetY.animateTo(-20f, animationSpec = tween(150))
                    offsetY.animateTo(0f, animationSpec = tween(150))
                }
            }
        }
        content(
            Modifier.offset(x=offsetX.value.dp, y = offsetY.value.dp)
        )
    }

/*
    @Composable
    fun flipWithJumpAnimation(shouldAnimate: Boolean): Modifier {
        val angleProgress = remember { Animatable(0f) }
        val density = LocalDensity.current

        LaunchedEffect(shouldAnimate) {
            if (shouldAnimate) {
                angleProgress.snapTo(0f)
                angleProgress.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }

        val radiusDp = 60.dp
        val angleDeg = angleProgress.value
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val offsetX: Dp
        val offsetY: Dp

        with(density) {
            val radiusPx = radiusDp.toPx()
            offsetX = (radiusPx * kotlin.math.cos(angleRad)).toFloat().toDp()
            offsetY = -(radiusPx * kotlin.math.sin(angleRad)).toFloat().toDp()
        }

        return Modifier
            .graphicsLayer {
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
            }
            .offset(x = offsetX, y = offsetY)
    }
*/
}

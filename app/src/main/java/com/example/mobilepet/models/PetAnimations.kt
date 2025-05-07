package com.example.mobilepet.models

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object PetAnimations {

    @Composable
    fun FeedAnimation(shouldAnimate: Boolean, content: @Composable (Modifier) -> Unit) {
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

    @Composable
    fun IdleAnimation(content: @Composable (Modifier) -> Unit) {
        val offsetY = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                offsetY.animateTo(-10f, animationSpec = tween(2000))
                offsetY.animateTo(10f, animationSpec = tween(2000))
                offsetY.animateTo(-5f, animationSpec = tween(2000))
                offsetY.animateTo(5f, animationSpec = tween(2000))
            }
        }
        content(
            Modifier.offset(y = offsetY.value.dp)
        )
    }

}

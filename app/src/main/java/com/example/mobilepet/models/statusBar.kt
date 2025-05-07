package com.example.mobilepet.models

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobilepet.R

@Composable
fun StatusBarsColumn(mood: Int, energy: Int, hunger: Int) {

    // Määrittää kuvakkeen mielialamittarille riippuen arvosta
    val moodIcon = when {
            mood > 70 -> painterResource(id = R.drawable.happy)
            mood > 40 -> painterResource(id = R.drawable.neutral)
            else -> painterResource(id = R.drawable.sad)
    }

    // Määrittää värit mittareille riippuen arvoista
    val moodColor = when {
        mood > 70 -> Color.Green
        mood > 40 -> Color.Yellow
        else -> Color.Red
    }
    val energyColor = when {
        energy > 70 -> Color.Green
        energy > 40 -> Color.Yellow
        else -> Color.Red
    }
    val hungerColor = when {
        hunger < 30 -> Color.Green
        hunger < 60 -> Color.Yellow
        else -> Color.Red
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        StatusBarRow(progress = mood / 100f, icon = moodIcon, color = moodColor)
        StatusBarRow(progress = energy / 100f, icon = painterResource(id = R.drawable.energy), color = energyColor)
        StatusBarRow(progress = hunger / 100f, icon = painterResource(id = R.drawable.drumstick), color = hungerColor)
    }
}

@Composable
fun StatusBarRow(progress: Float, icon: Painter, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 8.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
    }
}

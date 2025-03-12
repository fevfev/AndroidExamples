package com.knyazev.lingualearn.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt


@Composable
fun AnimatedCoinText(amount: Long, startPosition: Offset) {

    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(key1 = amount) {
        offsetY.animateTo(
            targetValue = -50f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    Text(
        text = "+$amount",
        color = Color.Yellow.copy(alpha = alpha.value),
        modifier = Modifier
            .offset {
                IntOffset(
                    startPosition.x.roundToInt(),
                    startPosition.y.roundToInt() + offsetY.value.roundToInt()
                )
            },
        style = androidx.compose.ui.text.TextStyle(
            fontSize = androidx.compose.ui.unit.TextUnit(15f, androidx.compose.ui.unit.TextUnitType.Sp),
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(2f, 2f),
                blurRadius = 2f
            )
        )
    )
}
package com.knyazev.lingualearn.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Particle(
    val color: Color,
    val startPosition: Offset,
    val direction: Offset,
    val speed: Float,
    val size: Float,
    val lifetime: Int
)

@Composable
fun ParticleSystem(
    particles: List<Particle>,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            ParticleView(particle = particle)
        }
    }
}
@Composable
fun ParticleView(particle: Particle) {
    val alpha = remember { Animatable(1f) }
    val position = remember { Animatable(particle.startPosition) }

    LaunchedEffect(key1 = particle) {
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = particle.lifetime)
        )
        position.animateTo(
            targetValue = particle.startPosition + particle.direction * particle.speed * (particle.lifetime / 1000f),
            animationSpec = tween(durationMillis = particle.lifetime)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = particle.color.copy(alpha = alpha.value),
            center = position.value,
            radius = particle.size / 2f
        )
    }
}
fun createParticles(center: Offset): List<Particle> {
    val particles = mutableListOf<Particle>()
    repeat(10) {
        val direction = Offset(
            x = Random.nextFloat() * 2f - 1f,
            y = Random.nextFloat() * 2f - 1f
        ).run {
            this / this.getDistance()
        }
        val speed = Random.nextFloat() * 50f + 50f
        val size = Random.nextFloat() * 10f + 5f
        val color = listOf(
            Color.Yellow,
            Color.Red,
            Color.Cyan,
            Color(0xFFFFA500)
        ).random()

        particles.add(
            Particle(
                color = color,
                startPosition = center,
                direction = direction,
                speed = speed,
                size = size,
                lifetime = 500
            )
        )
    }
    return particles
}
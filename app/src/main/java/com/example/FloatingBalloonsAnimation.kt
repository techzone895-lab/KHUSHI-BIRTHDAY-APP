package com.example

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class BalloonState(screenWidthDp: Int) {
    val xOffset = Random.nextInt(10, if (screenWidthDp > 60) screenWidthDp - 50 else 300)
    val delay = Random.nextInt(0, 4000)
    val duration = Random.nextInt(4000, 9000)
    val size = Random.nextInt(40, 80)
    val emoji = listOf("🎈", "🎈", "🎈", "🎈", "🎈", "🎉", "✨", "🎂").random()
    val wobbleDuration = Random.nextInt(1500, 3000)
    val wobbleAmount = Random.nextInt(10, 40).toFloat()
}

@Composable
fun FloatingBalloonsAnimation(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenWidth = maxWidth.value.toInt()
        val screenHeight = maxHeight.value.toInt()
        
        val balloons = remember(screenWidth, screenHeight) {
            List(25) { BalloonState(screenWidth) }
        }
        
        val infiniteTransition = rememberInfiniteTransition(label = "balloons")
        
        balloons.forEachIndexed { index, state ->
            val yOffset by infiniteTransition.animateFloat(
                initialValue = screenHeight.toFloat() + 150f,
                targetValue = -150f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = state.duration, delayMillis = state.delay, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "balloonY_$index"
            )
            
            val xWobble by infiniteTransition.animateFloat(
                initialValue = -state.wobbleAmount,
                targetValue = state.wobbleAmount,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = state.wobbleDuration, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "balloonWobble_$index"
            )
            
            Text(
                text = state.emoji,
                fontSize = state.size.sp,
                modifier = Modifier
                    .offset(x = (state.xOffset + xWobble).dp, y = yOffset.dp)
                    .alpha(0.85f)
            )
        }
    }
}

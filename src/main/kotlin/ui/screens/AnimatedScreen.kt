package ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnimatedScreen(content: @Composable () -> Unit) {
    val transition = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        transition.animateTo(1f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = transition.value
                translationY = (1 - transition.value) * 50
            }
    ) {
        content()
    }
}
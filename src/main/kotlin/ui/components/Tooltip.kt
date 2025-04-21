package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun Tooltip(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { showTooltip = true },
                    )
                }
        ) {
            content()
        }

        if (showTooltip) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-32).dp)
                    .background(
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(1.dp, MaterialTheme.colors.onSurface.copy(0.1f), RoundedCornerShape(4.dp))
                    .padding(4.dp)
            ) {
                Text(text, style = MaterialTheme.typography.caption)
            }
        }
    }
}

// Для использования:
//Tooltip(text = "Показать детали") {
//    Icon(Icons.Default.Info, "Детали")
//}
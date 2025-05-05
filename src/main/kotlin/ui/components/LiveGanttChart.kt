package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import model.Event
import utils.TimeTicker

@Composable
fun LiveGanttChart(events: List<Event>) {
//    var now by remember { mutableStateOf(System.currentTimeMillis()) }
//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(500)
//            now = System.currentTimeMillis()
//        }
//    } // у нас и так есть currentTime, добавил т к хз за что он отвечает и стоит ли его трогать
    val ticker = remember { TimeTicker() }
    val now by ticker.now.collectAsState()
    val visibleRange = remember { mutableStateOf(10_000L) }
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            currentTime.value = System.currentTimeMillis()
        }
    }

    Column {
        Slider(
            value = visibleRange.value / 1000f,
            onValueChange = { visibleRange.value = (it * 1000).toLong() },
            valueRange = 1f..60f,
            steps = 59,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text("Time window: ${visibleRange.value / 1000} sec", modifier = Modifier.align(Alignment.CenterHorizontally))

        GanttChart(
            events = events.filter {
                it.server_time > currentTime.value - visibleRange.value
            },
            now
        )
    }
}
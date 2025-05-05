package utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing

class TimeTicker {
    private val _now = MutableStateFlow(System.currentTimeMillis())
    val now: StateFlow<Long> = _now

    private val delayTime = 500L

    init {
        CoroutineScope(Dispatchers.Swing).launch {
            while (true) {
                delay(delayTime)
                _now.value = System.currentTimeMillis()
            }
        }
    }
}
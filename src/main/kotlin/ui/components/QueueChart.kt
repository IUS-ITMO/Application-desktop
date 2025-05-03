package ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import model.Event


// Генерация карты очередей
fun generateQueueMap(events: List<Event>): Map<String, MutableList<Pair<Long, Int>>> {
    val queueMap = mutableMapOf<String, MutableList<Pair<Long, Int>>>() // name -> list of (time, size)

    var lastQueue: String? = null
    var lastSize: Int? = null

    for (event in events) {

        val queueName = event.name
        val eventTime = event.server_time
        val size = event.size

        when (event.event_code) {
            6 -> { // Регистрация очереди
                queueMap.putIfAbsent(queueName, mutableListOf())
            }
            7 -> { // Отправка элемента в очередь
                if (lastQueue == queueName && lastSize != null) {
                    queueMap.computeIfAbsent(queueName) { mutableListOf() }
                        // Правильно: время (eventTime) — на первом месте, размер очереди — на втором
                        .add(eventTime to lastSize)
                }
            }

            8 -> { // Получение элемента из очереди
                if (lastQueue == queueName && lastSize != null) {
                    queueMap.computeIfAbsent(queueName) { mutableListOf() }
                        // То же исправление: сначала время, потом размер очереди
                        .add(eventTime to lastSize)
                }
            }

        }
    }

    return queueMap
}
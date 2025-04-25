package model

import kotlinx.serialization.Serializable

@Serializable
data class RealtimeConfig(
    val websocketUrl: String = "ws://localhost:8080/events",
    val bufferSize: Int = 1000,
    val reconnectDelay: Long = 3000
)

class RealtimeEventBuffer(private val capacity: Int) {
    private val buffer = ArrayDeque<Event>(capacity)
    var onBufferUpdated: () -> Unit = {}

    fun add(event: Event) {
        if (buffer.size >= capacity) {
            buffer.removeFirst()
        }
        buffer.addLast(event)
        onBufferUpdated()
    }

    fun getEvents(): List<Event> = buffer.toList()
}
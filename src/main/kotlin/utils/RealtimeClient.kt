package utils

import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import model.Event
import model.RealtimeConfig
import model.RealtimeEventBuffer

class RealtimeClient(
    private val config: RealtimeConfig,
    private val buffer: RealtimeEventBuffer
) {
    private var socket: WebSocketSession? = null
    private var isActive = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun connect() {
        if (isActive) return
        isActive = true

        scope.launch {
            while (isActive) {
                try {
                    HttpClient(CIO).webSocket(config.websocketUrl) {
                        socket = this
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val event = Json.decodeFromString<Event>(frame.readText())
                                    buffer.add(event)
                                }
                                is Frame.Close -> break
                                else -> Unit
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("WebSocket error: ${e.message}")
                }

                if (isActive) delay(config.reconnectDelay)
            }
        }
    }

    suspend fun disconnect() {
        isActive = false
        try {
            socket?.close()
        } catch (e: Exception) {
            println("Error closing socket: ${e.message}")
        }
        scope.coroutineContext.cancelChildren()
    }
}
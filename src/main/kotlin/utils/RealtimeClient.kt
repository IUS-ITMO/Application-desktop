package utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import model.Event
import model.RealtimeConfig
import model.RealtimeEventBuffer
import model.EventData
import utils.JsonParser.parseEvents
import kotlin.time.Duration.Companion.milliseconds

class RealtimeClient(
    private val config: RealtimeConfig,
    private val buffer: RealtimeEventBuffer,
    private val scope: CoroutineScope
) {
    private var socket: DefaultClientWebSocketSession? = null
    private var isActive = false
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 15_000L.milliseconds
        }
    }
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private fun processWebSocketMessage(text: String): List<Event> {
        println("Received raw message: $text")
        return try {
            val events = parseEvents(text)
            println("Successfully parsed ${events.size} events")
            events
        } catch (e: Exception) {
            println("Error parsing message: ${e.message}")
            emptyList()
        }
    }

    fun connect() {
        if (isActive) return
        isActive = true

        scope.launch {
            while (isActive) {
                try {
                    client.webSocket(config.websocketUrl) {
                        socket = this
                        println("WebSocket connected to ${config.websocketUrl}")

                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val text = frame.readText()
                                    processWebSocketMessage(text).forEach { event ->
                                        buffer.add(event)
                                    }
                                    try {
                                        val message = json.decodeFromString<EventData>(text)
                                        message.events.forEach { event ->
                                            buffer.add(event)
                                        }
                                    } catch (e: Exception) {
                                        println("Error parsing message: ${e.message}\nRaw data: $text")
                                    }
                                }
                                is Frame.Close -> {
                                    val reason = closeReason.await()
                                    println("WebSocket closed: ${reason?.message}")
                                    break
                                }
                                else -> Unit
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("WebSocket error: ${e.message}")
                } finally {
                    socket = null
                    if (isActive) {
                        println("Reconnecting in ${config.reconnectDelay}ms...")
                        delay(config.reconnectDelay)
                    }
                }
            }
        }
    }

    suspend fun disconnect() {
        isActive = false
        try {
            socket?.close(CloseReason(CloseReason.Codes.NORMAL, "Client closed"))
        } catch (e: Exception) {
            println("Error closing socket: ${e.message}")
        }
        client.close()
        scope.coroutineContext.cancelChildren()
    }
}
package utils

import kotlinx.serialization.json.Json
import model.Event
import model.EventData
import java.io.File

object JsonLoader {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun loadEventsFromFile(path: String): List<Event> {
        return try {
            val jsonString = File(path).readText()
            println("Raw JSON content (first 200 chars):\n${jsonString.take(200)}...")

            try {
                val eventData = json.decodeFromString<EventData>(jsonString)
                if (eventData.events.isEmpty()) {
                    println("Warning: Empty 'data' array in JSON")
                }
                eventData.events
            } catch (e: Exception) {
                println("Failed to parse as EventData, trying as direct array: ${e.message}")
                json.decodeFromString<List<Event>>(jsonString)
            }
        } catch (e: Exception) {
            println("Error loading JSON: ${e.message}\n${e.stackTraceToString()}")
            emptyList()
        }
    }
}

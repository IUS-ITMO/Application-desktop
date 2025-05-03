package utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import model.Event
import model.EventData
import model.NumericBooleanSerializer

object JsonParser {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        explicitNulls = false
        serializersModule = SerializersModule {
            contextual(Boolean::class, NumericBooleanSerializer)
        }
    }


    fun parseEvents(jsonString: String): List<Event> {
        return try {
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
            println("Error parsing events from json" + e.message)
            emptyList()
        }
    }
}



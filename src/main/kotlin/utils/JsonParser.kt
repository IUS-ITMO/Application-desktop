package utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import model.Event
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
            json.decodeFromString<List<Event>>(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }
}



package utils

import kotlinx.serialization.json.Json
import java.io.File
import model.EventData

object JsonLoader {
    fun loadEventsFromFile(path: String): EventData {
        return try {
            val jsonString = File(path).readText()
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                allowStructuredMapKeys = true
            }
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            println("Error loading JSON file: ${e.message}")
            throw e
        }
    }
}
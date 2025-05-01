package utils

import kotlinx.serialization.json.Json
import model.Event
import model.ExportData
import java.io.File
import java.io.IOException

object ImportUtils {
    private val json = Json { ignoreUnknownKeys = true }

    fun importFromJson(jsonString: String): List<Event> {
        return try {
            json.decodeFromString<ExportData>(jsonString).events
        } catch (e: Exception) {
            throw IOException("Invalid JSON format: ${e.message}")
        }
    }

    fun loadFromFile(file: File): List<Event> {
        return importFromJson(file.readText())
    }

    fun importFromCsv(csvString: String): List<Event> {
        val lines = csvString.lines()
        if (lines.isEmpty()) return emptyList()

        val headers = lines[0].split(",")
        return lines.drop(1).mapNotNull { line ->
            val values = line.split(",")
            if (values.size != headers.size) return@mapNotNull null

            Event(
                id = values[0].toIntOrNull() ?: -1,
                event_code = values[1].toIntOrNull() ?: 0,
                name = values[2].removeSurrounding("\""),
                server_time = values[3].toLongOrNull() ?: 0L,
                priority = values[4].toIntOrNull(),
                stack_size = values[5].toIntOrNull(),
                is_static = values[6].toBooleanStrictOrNull() == true,
                core_id = values[7].toIntOrNull()
            )
        }
    }
}
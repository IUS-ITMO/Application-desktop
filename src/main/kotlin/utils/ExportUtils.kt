package utils

import kotlinx.serialization.json.Json
import model.Event
import model.ExportData
import model.Metadata
import java.io.File
import java.io.IOException

object ExportUtils {
    private val json = Json { prettyPrint = true }

    fun exportToJson(events: List<Event>): String {
        return json.encodeToString(ExportData.serializer(), ExportData(Metadata(), events))
    }

    fun saveToFile(content: String, file: File) {
        try {
            file.writeText(content)
        } catch (e: Exception) {
            throw IOException("Failed to save file: ${e.message}")
        }
    }

    fun exportToCsv(events: List<Event>): String {
        val headers = listOf(
            "id", "event_code", "name", "server_time",
            "priority", "stack_size", "is_static", "core_id"
        ).joinToString(",")

        val rows = events.joinToString("\n") { event ->
            listOf(
                event.id,
                event.event_code,
                "\"${event.name}\"",
                event.server_time,
                event.priority ?: "",
                event.stack_size ?: "",
                event.is_static ?: "",
                event.core_id ?: ""
            ).joinToString(",")
        }

        return "$headers\n$rows"
    }
}
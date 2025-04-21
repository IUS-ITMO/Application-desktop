package model

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val metadata: Metadata,
    val events: List<Event>
)

@Serializable
data class Metadata(
    val exportDate: Long = System.currentTimeMillis(),
    val source: String = "FreeRTOS Event Viewer",
    val version: String = "1.0"
)
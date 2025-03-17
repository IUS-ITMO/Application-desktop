data class FreeRTOSEvent(
    val taskName: String, // Имя задачи
    val eventType: String, // Тип события (например, "Created", "Switched", "Deleted")
    val timestamp: Long // Временная метка события
)
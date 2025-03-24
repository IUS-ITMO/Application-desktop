package model

import kotlinx.serialization.Serializable

@Serializable
data class EventData(
    val data: List<Event> = emptyList()
)

@Serializable
data class Event(
    val id: Int = -1,
    val event_code: Int = 0,
    val name: String = "",
    val server_time: Long = 0L,
    val priority: Int? = null,
    val stack_size: Int? = null,
    val is_static: Boolean? = null,
    val core_id: Int? = null
) {
    val eventType: String
        get() = when(event_code) {
            1 -> "System Start"
            2 -> "Task Created"
            3 -> "Context Switch"
            4 -> "Task Deleted"
            else -> "Unknown Event"
        }
}
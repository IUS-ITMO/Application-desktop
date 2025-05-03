package model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class EventData(
    @SerialName("data")
    val events: List<Event> = emptyList(),

    @SerialName("type")
    val messageType: String? = null,

    @SerialName("timestamp")
    val serverTimestamp: Long? = null
)

@Serializable
data class TaskStats(
    val name: String,
    val totalRuntime: Long,
    val averageRuntime: Long,
    val maxRuntime: Long,
    val minRuntime: Long,
    val switchCount: Int,
    val priority: Int?,
    val stackSize: Int?,
    val creationTime: Long
)

@Serializable
data class CpuLoadPoint(
    val time: Long,
    val load: Float  // 0..1
)

@Serializable
data class Event(
    val id: Int = -1,
    val event_code: Int = 0,
    val name: String = "",
    val server_time: Long = 0L,
    val priority: Int? = null,
    val stack_size: Int? = null,

    @Serializable(with = NumericBooleanSerializer::class)
    val is_static: Boolean = false,

    val core_id: Int? = null,
    val success: Int? = null,
    val size: Int? = null,
    val capacity: Int? = null
) {
    val eventType: String
        get() = when(event_code) {
            1 -> "System Start"
            2 -> "Task Created"
            3 -> "Context Switch"
            4 -> "Task Deleted"
            6 -> "Queue Created"
            7 -> "Queue Receive"
            8 -> "Queue Send"
            else -> "Unknown Event"
        }
}

object NumericBooleanSerializer : KSerializer<Boolean> {
    override val descriptor = PrimitiveSerialDescriptor("NumericBoolean", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeInt(if (value) 1 else 0)
    }

    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeInt() == 1
    }
}

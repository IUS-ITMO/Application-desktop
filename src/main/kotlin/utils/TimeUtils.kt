package utils

object TimeUtils {

    fun formatDuration(millis: Long): String {
        return when {
            millis < 1000 -> "$millis ms"
            millis < 60000 -> "%.1f s".format(millis / 1000.0)
            else -> {
                val minutes = millis / 60000
                val seconds = (millis % 60000) / 1000
                "$minutes m $seconds s"
            }
        }
    }
}
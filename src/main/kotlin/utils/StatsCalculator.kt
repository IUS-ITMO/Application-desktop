package utils

import model.CpuLoadPoint
import model.Event
import model.TaskStats

object StatsCalculator {
    fun calculateTasksStats(events: List<Event>): List<TaskStats> {
        val taskMap = mutableMapOf<String, TaskStatsBuilder>()

        events.filter { it.event_code == 2 }.forEach { event ->
            taskMap[event.name] = TaskStatsBuilder(
                name = event.name,
                priority = event.priority,
                stackSize = event.stack_size,
                creationTime = event.server_time
            )
        }

        var lastSwitchTime: Long? = null
        var lastTask: String? = null

        events.filter { it.event_code == 3 }.forEach { event ->
            if (lastSwitchTime != null) {
                val duration = event.server_time - lastSwitchTime!!
                taskMap[lastTask]?.addRuntime(duration)
            }
            lastSwitchTime = event.server_time
            lastTask = event.name
        }

        return taskMap.values.map { it.build() }
    }
    fun calculateCpuLoad(events: List<Event>, windowSizeMs: Long = 1000): List<CpuLoadPoint> {
        if (events.isEmpty()) return emptyList()

        val contextSwitches = events.filter { it.event_code == 3 }
        if (contextSwitches.size < 2) return emptyList()

        val minTime = contextSwitches.first().server_time
        val maxTime = contextSwitches.last().server_time

        val result = mutableListOf<CpuLoadPoint>()
        var windowStart = minTime

        while (windowStart < maxTime) {
            val windowEnd = windowStart + windowSizeMs
            val windowSwitches = contextSwitches.filter {
                it.server_time in windowStart..windowEnd
            }

            if (windowSwitches.size >= 2) {
                var busyTime = 0L
                for (i in 0 until windowSwitches.size - 1) {
                    val current = windowSwitches[i]
                    val next = windowSwitches[i + 1]
                    if (current.name != "idle") {
                        busyTime += next.server_time - current.server_time
                    }
                }

                val load = busyTime.toFloat() / windowSizeMs.toFloat()
                result.add(CpuLoadPoint(windowStart, load.coerceIn(0f, 1f)))
            }

            windowStart = windowEnd
        }

        return result
    }
}

private class TaskStatsBuilder(
    val name: String,
    val priority: Int?,
    val stackSize: Int?,
    val creationTime: Long
) {
    private val runtimes = mutableListOf<Long>()
    private var switchCount = 0

    fun addRuntime(duration: Long) {
        runtimes.add(duration)
        switchCount++
    }

    fun build(): TaskStats {
        val total = runtimes.sum()
        return TaskStats(
            name = name,
            totalRuntime = total,
            averageRuntime = if (runtimes.isNotEmpty()) total / runtimes.size else 0,
            maxRuntime = runtimes.maxOrNull() ?: 0,
            minRuntime = runtimes.minOrNull() ?: 0,
            switchCount = switchCount,
            priority = priority,
            stackSize = stackSize,
            creationTime = creationTime
        )
    }
}


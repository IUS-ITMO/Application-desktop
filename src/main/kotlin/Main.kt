import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    val appState = AppState()

    appState.addEvent(FreeRTOSEvent("Task1", "Created", System.currentTimeMillis()))
    appState.addEvent(FreeRTOSEvent("Task2", "Switched", System.currentTimeMillis() + 1000))

    Window(
        onCloseRequest = ::exitApplication,
        title = "FreeRTOS Tracker"
    ) {
        MaterialTheme {
            TaskEventList(appState)
        }
    }
}

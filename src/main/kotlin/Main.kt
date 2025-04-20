import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import state.AppState
import ui.screens.App
import utils.JsonLoader

fun main() = application {
    val appState = AppState(emptyList())

    loadDataAsync(appState)

    Window(
        onCloseRequest = ::exitApplication,
        title = "FreeRTOS Event Viewer"
    ) {
        MaterialTheme {
//            MainScreen(appState)
            App(appState)
        }
    }
}

private fun loadDataAsync(appState: AppState) {
    try {
        val eventData = JsonLoader.loadEventsFromFile("src/main/resources/events.json")
        if (eventData.data.isEmpty()) {
            println("Warning: Loaded empty event list")
        }
        appState.events = eventData.data
    } catch (e: Exception) {
        println("Failed to load data: ${e.message}")
        e.printStackTrace()
        appState.errorMessage = "Failed to load data: ${e.message}"
    }
}

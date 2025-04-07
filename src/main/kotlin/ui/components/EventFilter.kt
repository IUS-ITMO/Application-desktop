package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import state.AppState

@Composable
fun EventFilter(appState: AppState) {
    Column {
        // Фильтр по типу события
        DropdownMenu(
            options = listOf("All", "System Start", "Task Created", "Context Switch", "Task Deleted"),
            selectedOption = appState.selectedEventType ?: 0,
            onSelect = { appState.selectedEventType = it }
        )

        // Фильтр по имени задачи
        TextField(
            value = appState.selectedTaskName ?: "",
            onValueChange = { appState.selectedTaskName = it },
            label = { Text("Filter by Task Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Фильтр по времени
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Time Range:")
            // Вы можете добавить UI для ввода времени в формате timestamp (например, два поля для "от" и "до")
        }
    }
}

@Composable
fun DropdownMenu(options: List<String>, selectedOption: Int, onSelect: (Int?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = options[selectedOption],
            modifier = Modifier.clickable(onClick = { expanded = true }),
            style = MaterialTheme.typography.body1
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    if (option == "All") {
                        onSelect(null) // Устанавливаем null для "All"
                    } else {
                        onSelect(index)
                    }
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}

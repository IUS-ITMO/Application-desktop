package ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import state.AppState

@Composable
fun EventFilter(appState: AppState) {
    Column {
//        SearchBar(
//            searchQuery = appState.searchQuery,
//            onSearchQueryChange = { appState.searchQuery = it }
//        )
        TextField(
            value = appState.selectedTaskName ?: "",
            onValueChange = { appState.selectedTaskName = it },
            label = { Text("Filter by Task Name") },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            options = listOf("All", "System Start", "Task Created", "Context Switch", "Task Deleted"),
            selectedOption = appState.selectedEventType ?: 0,
            onSelect = { appState.selectedEventType = it }
        )

        val coreIds = remember { appState.events.mapNotNull { it.core_id }.distinct().sorted() }
        if (coreIds.isNotEmpty()) {
            DropdownMenu(
                options = listOf("All cores") + coreIds.map { "Core $it" },
                selectedOption = appState.selectedCoreId?.let { coreIds.indexOf(it) + 1 } ?: 0,
                onSelect = { index ->
                    if (index != null) {
                        appState.selectedCoreId = if (index == 0) null else coreIds[index - 1]
                    }
                }
            )
        }

        TimeRangeFilter(appState)
    }
}

@Composable
fun DropdownMenu(
    options: List<String>,
    selectedOption: Int,
    onSelect: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(options[selectedOption])
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(onClick = {
                    onSelect(if (option == "All") null else index)
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}

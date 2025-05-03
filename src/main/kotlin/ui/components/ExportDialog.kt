package ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Event
import utils.ExportUtils
import java.io.File
import javax.swing.JFileChooser

@Composable
fun ExportDialog(
    events: List<Event>,
    onDismiss: () -> Unit,
    onSave: (File) -> Unit
) {
    var selectedFormat by remember { mutableStateOf(0) }
    val formats = listOf("JSON", "CSV")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Data") },
        text = {
            Column {
                Text("Select export format:")
                Spacer(Modifier.height(8.dp))
                Column {
                    formats.forEachIndexed { index, format ->
                        Row {
                            RadioButton(
                                selected = selectedFormat == index,
                                onClick = { selectedFormat = index }
                            )
                            Text(format, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val content = when (selectedFormat) {
                    0 -> ExportUtils.exportToJson(events)
                    1 -> ExportUtils.exportToCsv(events)
                    else -> ""
                }
                val file = saveFile("events.${formats[selectedFormat].lowercase()}")
                file?.let {
                    ExportUtils.saveToFile(content, it)
                    onSave(it)
                }
            }) {
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun saveFile(defaultFileName: String): File? {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "Save File"
        selectedFile = File(defaultFileName)
    }
    return if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile
    } else {
        null
    }
}
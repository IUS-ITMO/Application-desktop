package ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import model.Event
import utils.ImportUtils
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onImport: (List<Event>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Data") },
        text = { Text("Select file to import:") },
        confirmButton = {
            Button(onClick = {
                val file = openFile(listOf("*.json", "*.csv"))
                file?.let { selectedFile ->
                    try {
                        val events = if (selectedFile.extension == "json") {
                            ImportUtils.loadFromFile(selectedFile)
                        } else {
                            ImportUtils.importFromCsv(selectedFile.readText())
                        }
                        onImport(events)
                    } catch (e: Exception) {
                        // Обработка ошибок
                    }
                }
            }) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun openFile(extensions: List<String>): File? {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "Open File"
        fileFilter = FileNameExtensionFilter(
            "Supported files (${extensions.joinToString(", ")})",
            *extensions.map { it.removePrefix("*.") }.toTypedArray()
        )
    }
    return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile
    } else {
        null
    }
}
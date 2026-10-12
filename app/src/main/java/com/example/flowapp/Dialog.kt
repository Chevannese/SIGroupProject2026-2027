package com.example.flowapp

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(title: String, text: String, onDismiss: () -> Unit = {}) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "OK")
            }
        },
        onDismissRequest = onDismiss
    )
}
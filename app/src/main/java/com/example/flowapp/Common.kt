package com.example.flowapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.annotation.NonNull
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeOptions
import androidx.core.app.NotificationCompat
import com.example.flowapp.ui.theme.flowColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.LinkedList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackBar(title: String, onBack: () -> Unit, content: @Composable () -> Unit = {}) {
    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

@Composable fun DropdownField(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.flowColors.fieldFill)
                    .clickable { expanded = true }
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected,
                    style = MaterialTheme.typography.titleSmall, // 15sp SemiBold
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(28.dp)
                )
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                for (option in options) {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

fun toast(text: String) {
    Toast.makeText(MainActivity.instance.applicationContext, text, Toast.LENGTH_SHORT).show()
}

// you can just run it and it sends a notification
suspend fun sendNotification(trn: String, title: String, message: String, channel: NotificationType = NotificationType.GENERAL, context: Context = MainActivity.instance.applicationContext) = withContext(
    Dispatchers.IO) {
    val id = when (channel) {
        NotificationType.QUEUE -> queueNotificationChannelId
        NotificationType.GENERAL -> generalNotificationChannelId
    }
    val notification = NotificationCompat.Builder(context, id)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    MainActivity.instance.notificationManager.notify(1, notification)

    try {
        val file = File(MainActivity.instance.applicationContext.filesDir, "notifications.txt")

        file.appendText("$trn\t$title\t$message\n")
    }
    catch (e: IOException) {
        Log.d("ERROR", "Error saving notification")
    }
}

suspend fun getNotifications(trn: String): List<Array<String>> = withContext(Dispatchers.IO) {
    val notifications = LinkedList<Array<String>>()

    try {
        val file = File(MainActivity.instance.applicationContext.filesDir, "notifications.txt")

        if (file.exists()) {
            for (line in file.readLines()) {
                val split = line.split("\t")
                if (split[0] == trn) {
                    notifications.add(arrayOf(split[1], split[2]))
                }
            }
        }


    }
    catch (e: IOException) {
        Log.d("ERROR", "Error saving notification")
    }

    return@withContext notifications.toList()
}

suspend fun clearNotifications(trn: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val file = File(MainActivity.instance.applicationContext.filesDir, "notifications.txt")
        val updated = StringBuilder()

        if (file.exists()) {
            for (line in file.readLines()) {
                if (!line.startsWith(trn)) {
                    updated.appendLine(line)
                }
            }
        }

        file.writeText(updated.toString())

        return@withContext true
    }
    catch (e: IOException) {
        Log.d("ERROR", e.message.toString())
    }
    catch (e: Exception) {
        Log.d("ERROR", e.message.toString())
    }

    return@withContext false
}
package com.example.flowapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.flowColors
import kotlinx.coroutines.launch

@Composable
fun Notifications(onBack: () -> Unit, trn: String) {
    var notifications by remember { mutableStateOf(listOf<Array<String>>()) }
    var dismissText by remember { mutableStateOf("Dismiss All") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(trn) {
        notifications = getNotifications(trn)
    }

    BackBar("Notifications", onBack = onBack) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No notifications", style = MaterialTheme.typography.bodyMedium)
                }
            }
            else {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notifications) { notification ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = notification.getOrElse(0) { "" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notification.getOrElse(1) { "" },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                /*
                                IconButton(onClick = scope.launch { clearNotification() }) {

                                }
                                 */
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    scope.launch {
                        notifications = getNotifications(trn)
                    }
                },
                Modifier.fillMaxWidth()
            ) {
                Text("Refresh")
            }

            TextButton(
                onClick = {
                    when (dismissText) {
                        "Dismiss All" -> dismissText = "Are you sure?"
                        "Are you sure?" -> {
                            scope.launch {
                                if (clearNotifications(trn)) {
                                    notifications = listOf()
                                }
                                else {
                                    toast("An error occurred clearing notifications")
                                }

                                dismissText = "Dismiss All"
                            }
                        }
                    }
                },
                Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(239, 198, 198, 255))
            ) {
                Text(dismissText, color = Color.Red)
            }
        }
    }
}
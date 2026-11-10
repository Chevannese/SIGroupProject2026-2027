package com.example.flowapp

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme

@Composable
fun Home(trn: String, onChangeTab: (Int) -> Unit) {
    var showCardDialog by remember { mutableStateOf(false) }
    var savedCard by remember { mutableStateOf<String?>(null) }
    //val client = remember { Client() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeButton(
                "Join the queue",
                "Get a ticket for a branch or service",
                Icons.Default.People,
                Color(132, 180, 243, 255),
                onClick = {}
            )
            HomeButton(
                "Book an appointment",
                "Schedule an in person or home visit",
                Icons.Default.CalendarMonth,
                Color(160, 234, 161, 255),
                onClick = {}
            )
            HomeButton(
                "Make a payment",
                "Pay your bill easily and securely",
                Icons.Default.CalendarMonth,
                Color(245, 245, 165, 255),
                onClick = { onChangeTab(2) }
            )
            HomeButton(
                "View Services",
                "Explore all available services",
                Icons.Default.HomeRepairService,
                Color(217, 165, 245, 255),
                onClick = { onChangeTab(2) }
            )
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = rememberScrollState())
        )
    }


    if (showCardDialog) {
        if (!savedCard.isNullOrBlank()) {
            AlertDialog(
                onDismissRequest = { showCardDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Saved Card",
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = { Text("Saved Credit Card") },
                text = {
                    Column {
                        Text(
                            text = "Card Number:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = savedCard!!.substring(savedCard!!.length - 4),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showCardDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
        else {
            AddCard(
                onBack = {
                    //savedCard = client.cards(trn)
                    showCardDialog = false
                },
                trn = trn
            )
        }
    }
}

@Composable fun HomeButton(title: String, text: String, icon: ImageVector, colour: Color = Color.Black, onClick: () -> Unit) {
    Spacer(modifier = Modifier.size(20.dp))
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = colour.copy(alpha = 0.2f))) {
        Column {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp).background(colour, CircleShape).padding(10.dp)
                )
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(text, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Box(modifier = Modifier.padding(10.dp).align(Alignment.End).background(Color(0.0f, 0.0f, 0.0f, 0.2f),
                CircleShape
            )) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).background(colour, CircleShape).padding(10.dp)
                )
            }
        }
    }
}

// preview home
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    FlowAppTheme(darkTheme = true) {
        Home(trn = "123456789", {})
    }
}

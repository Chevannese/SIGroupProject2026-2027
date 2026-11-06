package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flowapp.ui.theme.FlowAppTheme
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(viewModel: TicketsViewModel = viewModel()) {
    val tickets by viewModel.tickets.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("All Tickets") }) }
    ) { padding ->
        if (tickets.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No tickets yet") }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets, key = { it.ticketNo }) { ticket ->
                    TicketCard(ticket)
                }
            }
        }
    }
}

@Composable
fun TicketCard(ticket: Ticket) {
    val fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a")

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ticket ${ticket.ticketNo}", style = MaterialTheme.typography.titleMedium)
                Text("Queue #${ticket.queueNumber}", style = MaterialTheme.typography.titleMedium)
            }
            Text("Service: ${ticket.service}")
            Text("Branch: ${ticket.branch}")
            Text("Status: ${ticket.ticketStatus.name.replace('_', ' ')}")
            Text("Payment: ${ticket.paymentStatus.name.replace('_', ' ')} (${ticket.paymentMethod})")
            Text("Cost: $${"%.2f".format(ticket.cost)}  |  Paid: $${"%.2f".format(ticket.totalPaid)}")
            Text(
                "Created: ${ticket.createdAt.format(fmt)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun TicketScreenPreview() {
    FlowAppTheme {
        TicketsScreen()
    }
}
package com.example.flowapp


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(tickets: List<Ticket>) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Tickets") }
            )
        }
    ) { padding ->

        if (tickets.isEmpty()) {

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tickets yet")
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
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
@Preview
@Composable
fun TicketScreenPreview() {
    FlowAppTheme {
        TicketsScreen(tickets = emptyList())
    }
}
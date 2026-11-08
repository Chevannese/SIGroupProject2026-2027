
package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLocale
import java.time.format.DateTimeFormatter

@Composable
fun TicketCard(ticket: Ticket) {

    val fmt = DateTimeFormatter.ofPattern(
        "dd MMM yyyy, h:mm a",
        LocalLocale.current.platformLocale
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Ticket ${ticket.ticketNo}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Text("Service: ${ticket.service}")

            Text("Branch: ${ticket.branch}")

            Text("Status: ${ticket.ticketStatus.replace('_', ' ')}")

            HorizontalDivider()

            Text(
                "Created: ${ticket.created.format(fmt)}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "Service Started: ${
                    ticket.serviceStartTime?.format(fmt) ?: "Not Started"
                }",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "Service Ended: ${
                    ticket.serviceEndTime?.format(fmt) ?: "Not Completed"
                }",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "Service Duration: ${ticket.serviceTime} minutes",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

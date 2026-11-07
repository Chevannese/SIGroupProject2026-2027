package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowapp.ui.theme.FlowAppTheme

@Composable
fun TicketCard(ticket: Ticket) {

    val fmt = SimpleDateFormat(
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

            Text(
                "Payment: ${ticket.paymentStatus.replace('_', ' ')} " +
                        "(${ticket.paymentMethod})"
            )

            Text(
                "Cost: $${"%.2f".format(ticket.cost)} | " +
                        "Paid: $${"%.2f".format(ticket.totalPaid)}"
            )

            Text(
                "Created: ${fmt.format(ticket.created)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

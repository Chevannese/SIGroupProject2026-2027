
package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Waiting(
    trn: String,
    onProceedToPayment: () -> Unit
) {
    val client = remember { Client() }

    var queueInfo by remember {
        mutableStateOf<QueueSnapshot?>(null)
    }

    var isRefreshing by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    // Load and refresh queue information.
    suspend fun refreshQueue() {
        isRefreshing = true

        queueInfo = withContext(Dispatchers.IO) {
            val currentInfo = client.getQueueSnapshot(trn)

            val branch = currentInfo.customerTicket?.branch

            if (branch != null) {
                client.updateQueue(branch)
            }

            client.getQueueSnapshot(trn)
        }

        isRefreshing = false
    }

    // Load queue information when the page opens.
    LaunchedEffect(trn) {
        refreshQueue()
    }

    val customerTicket = queueInfo?.customerTicket
    val nowServing = queueInfo?.nowServing
    val nextInLine = queueInfo?.nextInLine

    val isAtFront = customerTicket?.ticketStatus == "Being Served"


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {

        // Header and Refresh
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Waiting Room",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = {
                    scope.launch {
                        refreshQueue()
                    }
                },
                enabled = !isRefreshing,
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
                Text("Refresh", fontSize = 12.sp)
            }
        }

        // Scrollable queue information
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Waiting customer count
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Waiting customers",
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${queueInfo?.waitingCount ?: 0}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Customers Waiting",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Your Ticket
            if (customerTicket != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "YOUR TICKET",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = customerTicket.ticketNo,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = customerTicket.ticketStatus,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    ),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "Customers ahead: ${queueInfo?.customersAhead ?: 0}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Text(
                    text = "You currently have no active queue ticket.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Now Serving
            QueueInfoCard(
                title = "Now Serving",
                ticketNo = nowServing?.ticketNo ?: "Not Available",
                compact = true
            )

            // Next in Line
            QueueInfoCard(
                title = "Next in Line",
                ticketNo = nextInLine?.ticketNo ?: "Not Available",
                compact = true
            )
        }

        // Payment button stays at the bottom
        if (isAtFront) {
            Button(
                onClick = onProceedToPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF2E9D50)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Proceed to Payment",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}

@Composable
fun QueueInfoCard(
    title: String,
    ticketNo: String,
    compact: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                if (compact) 12.dp else 20.dp
            )
        ) {
            Text(
                text = title,
                fontSize = if (compact) 12.sp else 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(
                    if (compact) 4.dp else 10.dp
                )
            )

            Text(
                text = ticketNo,
                fontSize = if (compact) 22.sp else 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
// preview home
@Preview(showBackground = true)
@Composable
fun WaitingPreview() {
    FlowAppTheme(darkTheme = true) {
        Home(trn = "123456789", {}
        )
    }
}


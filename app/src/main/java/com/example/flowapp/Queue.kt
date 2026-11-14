package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Queues(
    trn: String,
    onBack: () -> Unit = {},
    startTab: Int = 0
) {
    val context = LocalContext.current

    var tickets by remember {
        mutableStateOf(emptyList<Ticket>())
    }

    var tab by rememberSaveable {
        mutableIntStateOf(startTab)
    }

    LaunchedEffect(tab, trn) {
        if (tab == 1) {
            tickets = withContext(Dispatchers.IO) {
                val client = Client()

                val branches = client.getAllTickets(context)
                    .filter { it.ticketStatus == "Being Served" }
                    .map { it.branch }
                    .distinct()

                branches.forEach { branch ->
                    client.updateQueue(branch)
                }

                client.getCustomerTickets(context, trn)
            }
        }
    }

    Column(modifier = Modifier.padding(20.dp)) {
        Spacer(Modifier.height(20.dp))

        PrimaryTabRow(
            selectedTabIndex = tab,
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
        ) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Get a ticket") }
            )

            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("My Ticket") }
            )
        }

        when (tab) {
            0 -> QueueTab(
                trn = trn,
                onTicketCreated = { newTicket ->
                    tickets = tickets.filterNot {
                        it.ticketNo == newTicket.ticketNo
                    } + newTicket

                    tab = 1
                }
            )

            1 -> TicketsScreen(tickets = tickets)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueTab(
    trn: String,
    onTicketCreated: (Ticket) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val services = listOf("Phone Service", "Internet Service")
    val branches = listOf("Portmore", "Kingston")

    var service by rememberSaveable { mutableStateOf(services[0]) }
    var branch by rememberSaveable { mutableStateOf(branches[0]) }
    var showCard by rememberSaveable { mutableStateOf(false) }
    var showAddCard by rememberSaveable { mutableStateOf(false) }
    var requesting by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Spacer(Modifier.height(28.dp))

            DropdownField(
                label = "Select Service",
                options = services,
                selected = service,
                onSelected = { service = it }
            )

            Spacer(Modifier.height(28.dp))

            DropdownField(
                label = "Select Branch",
                options = branches,
                selected = branch,
                onSelected = { branch = it }
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        requesting = true

                        try {
                            val client = connectClient()

                            if (client == null) {
                                toast("Unable to reach the server")
                                return@launch
                            }

                            // Check for an existing active ticket.
                            if (client.hasActiveTicket(trn)) {
                                toast("You already have an active ticket!")
                            } else {

                                // Check whether the customer has a saved payment method.
                                val card = client.cards(trn)

                                if (card.isNullOrBlank()) {
                                    showCard = true
                                } else {

                                    val newTicket = client.createTicket(
                                        trn = trn,
                                        service = service,
                                        branch = branch
                                    )

                                    if (newTicket == null) {
                                        toast("You already have an active ticket!")
                                    } else {

                                        // Reload the ticket to get its latest status.
                                        val updatedTicket = withContext(Dispatchers.IO) {
                                            client.getTicket(newTicket.ticketNo)
                                        }

                                        if (updatedTicket != null) {
                                            onTicketCreated(updatedTicket)
                                            toast("Ticket created successfully!")
                                        }
                                    }
                                }
                            }
                        } finally {
                            requesting = false
                        }
                    }
                },
                enabled = !requesting,
                modifier = Modifier
                    .scale(1.2f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Get Queue Number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        if (showAddCard) {
            AddCard(onBack = { showAddCard = false }, trn = trn)
        }
    }

    if (showCard) {
        NoCardDialog(
            onDismissRequest = { showCard = false },
            onConfirmRequest = {
                showCard = false
                showAddCard = true
            }
        )
    }
}

@Composable
fun NoCardDialog(onDismissRequest: () -> Unit = {}, onConfirmRequest: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("No Payment Method Found") },
        text = { Text("You do not have a payment method saved. Would you like to add a card now?") },
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text("Add Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
}

@Preview
@Composable
fun QueuesPreview() {
    FlowAppTheme {
        Queues(trn = "123456789")
    }
}

@Preview
@Composable
fun TicketPreview() {
    FlowAppTheme {
        Queues(trn = "123456789", startTab = 1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Dialog() {
    NoCardDialog()
}

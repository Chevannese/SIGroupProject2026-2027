
package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Payments(
    trn: String,
    padding: PaddingValues = PaddingValues.Zero
) {
    var showPay by remember { mutableStateOf(false) }
    var showAddCard by remember { mutableStateOf(false) }

    var hasCard by remember(trn) {
        mutableStateOf(!Client().cards(trn).isNullOrBlank())
    }

    var ticketDisplay by remember(trn) {
        mutableStateOf("")
    }

    val client = Client()

    // Retrieve customer receipts.
    val receipts = client.getCustomerReceipts(trn)

    // Retrieve tickets that can still be paid or cancelled.
    val tickets = client.getCustomerTickets(trn = trn)
        .filter { ticket ->

            val receipt = receipts.find {
                it.ticketNo == ticket.ticketNo
            }

            ticket.ticketStatus in listOf(
                "Waiting",
                "Being Served",
                "Served"
            ) && receipt?.paymentStatus != "Fully Paid"
        }

    // Retrieve the selected ticket.
    // If it is unavailable, select the first available ticket.
    val selectedTicket = tickets.find {
        it.ticketNo == ticketDisplay
    } ?: tickets.firstOrNull()

    val selectedReceipt = receipts.find {
        it.ticketNo == selectedTicket?.ticketNo
    }

    if (tickets.isEmpty()) {

        Text(
            text = "No tickets available for payment",
            modifier = Modifier.padding(padding)
        )

    } else {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            DropdownField(
                "Ticket",
                tickets.map { it.ticketNo },
                selectedTicket!!.ticketNo,
                {
                    ticketDisplay = it
                }
            )

            Button(
                onClick = {
                    showPay = true
                }
            ) {
                Text("View Ticket")
            }

            if (!hasCard) {

                Text("No card information found")

                Button(
                    onClick = {
                        showAddCard = true
                    }
                ) {
                    Text("Add Card")
                }
            }
        }
    }

    if (showAddCard) {
        AddCard(
            onBack = {
                showAddCard = false
                hasCard = !Client().cards(trn).isNullOrBlank()
            },
            trn = trn
        )
    }

    if (
        showPay &&
        selectedTicket != null &&
        selectedReceipt != null
    ) {

        Pay(
            ticket = selectedTicket,
            receipt = selectedReceipt,
            hasCard = hasCard,
            onDismissRequest = {
                showPay = false
                hasCard = !Client().cards(trn).isNullOrBlank()
            }
        )
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(
    ticket: Ticket,
    receipt: Receipt,
    hasCard: Boolean,
    onDismissRequest: () -> Unit
) {

    val context = LocalContext.current
    val client = remember { Client() }

    // Retrieve the customer's card using their TRN.
    val customerCard = remember(ticket.custID) {
        client.getAllCards(context).find {
            it.trn == ticket.custID
        }
    }

    var processing by remember {
        mutableIntStateOf(2)
    }

    var error by remember {
        mutableStateOf(false)
    }

    var showCancelConfirmation by remember {
        mutableStateOf(false)
    }

    var cancelError by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val amountDue = receipt.cost - receipt.totalPaid

    // Only unpaid tickets that are waiting can be cancelled.
    val canCancel =
        ticket.ticketStatus == "Waiting" &&
                receipt.totalPaid == 0.0

    // Only tickets currently being served or already served
    // can be paid.
    val canPay =
        ticket.ticketStatus in listOf("Being Served", "Served") &&
                receipt.paymentStatus != "Fully Paid" &&
                hasCard &&
                customerCard != null

    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {

        if (processing == 0) {

            // Loading Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

        } else if (processing == 1) {

            // Payment Result
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (error) {
                    Text(
                        "An error occurred. Payment failed.",
                        style = MaterialTheme.typography.titleLarge
                    )
                } else {
                    Text(
                        "Payment Successful",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text("Close")
                }
            }

        } else if (processing == 2) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Ticket Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onDismissRequest
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                // Ticket Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Ticket #${ticket.ticketNo}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text("Service: ${ticket.service}")
                        Text("Branch: ${ticket.branch}")
                        Text("Status: ${ticket.ticketStatus}")
                    }
                }

                // Payment Card Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Payment Card",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (customerCard != null) {

                            Text(
                                text = "Flow Card •••• ${customerCard.lastFourDigits}"
                            )

                            Text(
                                text = "Available Balance: \$%.2f".format(
                                    customerCard.balance
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )

                        } else {

                            Text(
                                text = "No payment card found.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Amount Due
                OutlinedTextField(
                    label = {
                        Text("Amount Due")
                    },
                    onValueChange = {},
                    value = "$%.2f".format(amountDue),
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Make Payment Button
                if (canPay) {

                    Button(
                        onClick = {

                            scope.launch {

                                processing = 0

                                val response = withContext(Dispatchers.IO) {

                                    client.makePayment(
                                        ticketNo = ticket.ticketNo,
                                        amount = amountDue,
                                        cardLastFourDigits = customerCard!!.lastFourDigits
                                    )
                                }

                                error = !response
                                processing = 1
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Make Payment")
                    }

                } else if (customerCard == null || !hasCard) {

                    Text(
                        text = "A payment card is required before making a payment.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Cancel Ticket Button
                if (canCancel) {

                    OutlinedButton(
                        onClick = {
                            showCancelConfirmation = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel Ticket")
                    }
                }
            }
        }
    }

    // Cancellation Confirmation Dialog
    if (showCancelConfirmation) {

        AlertDialog(
            onDismissRequest = {
                showCancelConfirmation = false
            },

            title = {
                Text("Cancel Ticket?")
            },

            text = {
                Text(
                    "Are you sure you want to cancel ticket " +
                            "${ticket.ticketNo}? This action cannot be undone."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showCancelConfirmation = false

                        scope.launch {

                            val response = withContext(Dispatchers.IO) {
                                client.cancelTicket(ticket.ticketNo)
                            }

                            if (response) {
                                onDismissRequest()
                            } else {
                                cancelError = true
                            }
                        }
                    }
                ) {
                    Text("Yes, Cancel")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showCancelConfirmation = false
                    }
                ) {
                    Text("Keep Ticket")
                }
            }
        )
    }

    // Cancellation Error Dialog
    if (cancelError) {

        AlertDialog(
            onDismissRequest = {
                cancelError = false
            },

            title = {
                Text("Cancellation Failed")
            },

            text = {
                Text("The ticket could not be cancelled.")
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        cancelError = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}


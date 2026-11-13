
package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
        mutableStateOf(false)
    }

    var receipts by remember(trn) {
        mutableStateOf(emptyList<Receipt>())
    }

    var tickets by remember(trn) {
        mutableStateOf(emptyList<Ticket>())
    }

    var ticketDisplay by remember(trn) {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var loadError by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    // Load the customer's receipts, tickets and card information.
    suspend fun refreshPayments() {
        isLoading = true
        loadError = false

        try {
            val result = withContext(Dispatchers.IO) {
                val client = Client()

                // Check the customer's tickets for expired service times.
                val customerTickets =
                    client.getCustomerTickets(trn = trn)

                customerTickets.forEach { ticket ->
                    client.markNoShow(ticket.ticketNo)
                }

                // Reload tickets after checking for No Shows.
                val updatedTickets =
                    client.getCustomerTickets(trn = trn)

                val customerReceipts =
                    client.getCustomerReceipts(trn)

                val customerHasCard =
                    !client.cards(trn).isNullOrBlank()

                Triple(
                    customerReceipts,
                    updatedTickets,
                    customerHasCard
                )
            }

            receipts = result.first

            // Only show tickets that have a matching receipt
            // and are still eligible for payment or cancellation.
            tickets = result.second.filter { ticket ->

                val receipt = result.first.find {
                    it.ticketNo == ticket.ticketNo
                }

                ticket.ticketStatus in listOf(
                    "Waiting",
                    "Being Served",
                    "Served"
                ) &&
                        receipt != null &&
                        receipt.paymentStatus != "Fully Paid"
            }

            hasCard = result.third

        } catch (e: Exception) {
            loadError = true
        } finally {
            isLoading = false
        }
    }

    // Load data when the Payments Page opens.
    LaunchedEffect(trn) {
        refreshPayments()
    }

    // Find the selected ticket and its corresponding receipt.
    val selectedTicket = tickets.find {
        it.ticketNo == ticketDisplay
    } ?: tickets.firstOrNull()

    val selectedReceipt = receipts.find {
        it.ticketNo == selectedTicket?.ticketNo
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Refresh button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = {
                    scope.launch {
                        refreshPayments()
                    }
                },
                enabled = !isLoading
            ) {
                Text("Refresh")
            }
        }

        if (isLoading) {

            CircularProgressIndicator()

        } else if (loadError) {

            Text(
                text = "Unable to load payment information.",
                color = MaterialTheme.colorScheme.error
            )

            Button(
                onClick = {
                    scope.launch {
                        refreshPayments()
                    }
                }
            ) {
                Text("Try Again")
            }

        } else if (tickets.isEmpty()) {

            Text("No tickets available for payment")

        } else {

            DropdownField(
                label = "Ticket",
                options = tickets.map { it.ticketNo },
                selected = selectedTicket!!.ticketNo,
                onSelected = {
                    ticketDisplay = it
                }
            )

            Button(
                onClick = {
                    showPay = true
                },
                enabled = selectedReceipt != null
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

    // Add Card screen
    if (showAddCard) {
        AddCard(
            onBack = {
                showAddCard = false

                scope.launch {
                    refreshPayments()
                }
            },
            trn = trn
        )
    }

    // Payment details
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

                // Reload receipts and tickets after payment
                // or ticket cancellation.
                scope.launch {
                    refreshPayments()
                }
            }
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(ticket: Ticket, receipt: Receipt, hasCard: Boolean, onDismissRequest: () -> Unit) {
    val client = remember { Client() }
    val scroll = rememberScrollState()

    // Retrieve the customer's card using their TRN.
    val customerCard = remember(ticket.custID) {
        client.getAllCards().find {
            it.trn == ticket.custID
        }
    }

    var processing by remember { mutableIntStateOf(2) }

    var error by remember { mutableStateOf(false) }

    var updatedReceipt by remember {
        mutableStateOf(receipt)
    }

    var showCancelConfirmation by remember { mutableStateOf(false) }

    var cancelError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val amountDue = receipt.subTotal - receipt.totalPaid
    // Only unpaid tickets that are waiting can be cancelled.
    val canCancel = ticket.ticketStatus == "Waiting" && receipt.totalPaid == 0.0

    // Only tickets currently being served or already served
    // can be paid.
    val canPay =
        ticket.ticketStatus in listOf("Being Served", "Served") &&
        receipt.paymentStatus != "Fully Paid" &&
        hasCard &&
        customerCard != null

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        if (processing == 0) {
            // Loading Indicator
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
        else if (processing == 1) {
            // Payment Result
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (error) {
                    Text(
                        text = "An error occurred. Payment failed.",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else {
                    Text(
                        text = "Payment Successful",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    // Receipt Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = "Receipt"
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "Payment Receipt",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider()

                            // Receipt Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Receipt Number")
                                Text(updatedReceipt.receiptNo)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ticket Number")
                                Text(updatedReceipt.ticketNo)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("TRN")
                                Text(updatedReceipt.trn)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Method")
                                Text(updatedReceipt.paymentMethod)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Status")
                                Text(updatedReceipt.paymentStatus)
                            }

                            HorizontalDivider()

                            Text(
                                text = "Payment Breakdown",
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Service Cost")
                                Text("$%.2f".format(updatedReceipt.cost))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("GCT (15%)")
                                Text("$%.2f".format(updatedReceipt.surcharge))
                            }

                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Subtotal",
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "$%.2f".format(updatedReceipt.subTotal),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Paid")

                                Text(
                                    "$%.2f".format(updatedReceipt.totalPaid),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Amount Remaining")

                                Text(
                                    "$%.2f".format(
                                        updatedReceipt.subTotal - updatedReceipt.totalPaid
                                    )
                                )
                            }
                        }
                    }
                }

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Close")
                }
            }
        }
        else if (processing == 2) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)/*.verticalScroll(scroll)*/,
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {

                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                        Text(
                            text = "Payment Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Service Cost")
                            Text("$%.2f".format(receipt.cost))
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GCT (15%)")
                            Text("$%.2f".format(receipt.surcharge))
                        }

                        HorizontalDivider()

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Subtotal",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$%.2f".format(receipt.subTotal),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid")
                            Text("$%.2f".format(receipt.totalPaid))
                        }

                        HorizontalDivider()

                        /*
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "Amount Due",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "$%.2f".format(amountDue),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                         */
                    }
                }

                // Amount Due
                OutlinedTextField(
                    label = { Text("Amount Due") },
                    onValueChange = {},
                    value = "$%.2f".format(amountDue),
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Make Payment Button
                if (canPay) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                processing = 0

                                val response = withContext(Dispatchers.IO) {
                                    client.makePayment(
                                        ticket.ticketNo,
                                        amountDue,
                                        customerCard!!.lastFourDigits
                                    )
                                }

                                error = !response

                                if (response) {
                                    updatedReceipt = withContext(Dispatchers.IO) {
                                        client.getCustomerReceipts(ticket.custID)
                                            .find { it.ticketNo == ticket.ticketNo }
                                            ?: receipt
                                    }
                                }

                                processing = 1
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Make Payment")
                    }
                }
                else if (customerCard == null || !hasCard) {
                    Text(
                        text = "A payment card is required before making a payment.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                // Cancel Ticket Button
                else if (canCancel) {
                    OutlinedButton(
                        onClick = { showCancelConfirmation = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
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
                Text("Are you sure you want to cancel ticket ${ticket.ticketNo}? This action cannot be undone.")
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
                            }
                            else {
                                cancelError = true
                            }
                        }
                    }
                ) {
                    Text("Yes, Cancel")
                }
            },

            dismissButton = {
                TextButton(onClick = { showCancelConfirmation = false }) {
                    Text("Keep Ticket")
                }
            }
        )
    }

    // Cancellation Error Dialog
    if (cancelError) {
        AlertDialog(
            onDismissRequest = { cancelError = false },
            title = { Text("Cancellation Failed") },
            text = { Text("The ticket could not be cancelled.") },
            confirmButton = {
                TextButton(onClick = { cancelError = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview
@Composable
fun PaymentPreview() {
    Payments(trn = "")
}
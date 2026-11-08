package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme
import com.example.flowapp.ui.theme.flowColors
import com.google.android.material.loadingindicator.LoadingIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@Composable
fun Payments(trn: String, padding: PaddingValues = PaddingValues.Zero) {

    var showPay by remember { mutableStateOf(false) }
    var showAddCard by remember { mutableStateOf(false) }

    var hasCard by remember {
        mutableStateOf(!Client().cards(trn).isNullOrBlank())
    }

    val tickets = Client().getCustomerTickets(trn = trn)
        .filter {
            it.ticketStatus in listOf(
                "Being Served",
                "Served"
            ) && it.paymentStatus != "Fully Paid"
        }

    var ticketDisplay by remember(trn) {
        mutableStateOf("")
    }

    // Retrieve the selected ticket.
    // If the previously selected ticket is unavailable,
    // automatically select the first available ticket.
    val selectedTicket = tickets.find {
        it.ticketNo == ticketDisplay
    } ?: tickets.firstOrNull()

    if (tickets.isEmpty()) {

        Text("No tickets available for payment")

    } else {

        if (hasCard) {

            Column(
                modifier = Modifier.padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    onClick = { showPay = true }
                ) {
                    Text("Pay")
                }
            }

        } else {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("No card information found")

                Button(
                    onClick = { showAddCard = true }
                ) {
                    Text("Add Card")
                }
            }
        }
    }

    if (showAddCard) {
        AddCard(
            onBack = { showAddCard = false },
            trn = trn
        )
    }

    if (showPay && selectedTicket != null) {

        Pay(
            selectedTicket,
            trn,
            onDismissRequest = {
                showPay = false
                hasCard = !Client().cards(trn).isNullOrBlank()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(ticket: Ticket, trn: String, onDismissRequest: () -> Unit) {
    var processing by remember { mutableIntStateOf(2) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // TODO: delete these later and switch to data from a ticket
    var amount: Double by remember { mutableDoubleStateOf(0.0) }
    var amountDue by remember { mutableDoubleStateOf(ticket.cost - ticket.totalPaid) }
    var remainingBalance by remember { mutableDoubleStateOf(amountDue - amount) }

    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        if (processing == 0) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        else if (processing == 1) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    when (error) {
                        false -> Text("Payed", style = MaterialTheme.typography.titleLarge)
                        true -> Text("An error occurred")
                    }
                    TextButton(onClick = onDismissRequest) {
                        Text("Close")
                    }
                }
            }
        }
        else if (processing == 2) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pay Ticket",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Ticket info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.flowColors.ticketFill)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Ticket #${ticket.ticketNo}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 2. Card Information
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CreditCard,
                                contentDescription = null,
                                Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Payment Card",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 3. Amount Paying
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        label = { Text("Amount Due") },
                        onValueChange = {},
                        value = "$%.2f".format(amountDue),
                        enabled = false,
                    )
                }

                /*
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        label = { Text("Amount paying:") },
                        value = "$%.2f".format(amount),
                        onValueChange = {
                            val amountValue = it.toDoubleOrNull()
                            if (amountValue != null) {
                                amount = amountValue
                                remainingBalance = amountDue - amount
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        label = { Text("Amount Remaining") },
                        onValueChange = {},
                        value = "$%.2f".format(remainingBalance),
                        enabled = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                */

                // Pay Button
                Button(
                    onClick = {
                        scope.launch {

                            processing = 0

                            val client = Client()

                            val response = withContext(Dispatchers.IO) {
                                delay(6.seconds)
                                client.makePayment(ticket.ticketNo, amountDue)
                            }

                            error = !response
                            processing = 1
                        }
                    }
                ) {
                    Text("Make Payment")
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PayPreview() {
    FlowAppTheme {
        Pay(
            ticket = "34567",
            trn = "123456789",
            onDismissRequest = {}
        )
    }
}
*/

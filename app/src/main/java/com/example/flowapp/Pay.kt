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

@Composable
fun Payments(trn: String) {
    var ticket by remember { mutableStateOf("") }
    var showPay by remember { mutableStateOf(false) }

    val tickets = Client().getCustomerTickets(trn = trn)
    val displayTickets = tickets.map { t -> t.ticketNo }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        DropdownField(
            "Ticket",
            displayTickets,
            ticket,
            { ticket = it }
        )

        Button(onClick = { showPay = true }) {
            Text("Pay")
        }
    }

    if (showPay) {
        Pay(ticket, trn, onDismissRequest = { showPay = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pay(ticket: String, trn: String, onDismissRequest: () -> Unit) {
    var processing by remember { mutableIntStateOf(2) }
    var error by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // TODO: delete these later and switch to data from a ticket
    var remainingBalance by remember { mutableDoubleStateOf(0.0) }
    var amount: Double by remember { mutableDoubleStateOf(0.0) }

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
                                text = "Ticket #${ticket}",
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
                        label = { Text("Amount paying:") },
                        value = "$%.2f".format(remainingBalance),
                        onValueChange = {
                            val amountValue = it.toDoubleOrNull()
                            when (amountValue) {
                                null -> toast("Invalid amount")
                                else -> amount = amountValue
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                // Pay Button
                Button(
                    onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                // TODO: Update ticket remaining amount
                                processing = 1
                                val client = Client()
                                val response = client.makePayment(ticket, amount ?: 0.0)

                                error = !response
                                processing = 2
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Pay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

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

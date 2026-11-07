package com.example.flowapp

import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Queues(
    trn: String = "123456789",
    onBack: () -> Unit = {},
    startTab: Int = 0
) {
    val context = LocalContext.current

    // Currently signed-in customer

    var tickets by rememberSaveable {
        mutableStateOf(emptyList<Ticket>())
    }

    var tab by rememberSaveable {
        mutableIntStateOf(startTab)
    }

    LaunchedEffect(trn) {
        tickets = withContext(Dispatchers.IO) {

            initializeDefaultTickets(context, trn)

            getCustomerTickets(context, trn)
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
            0 -> QueueTab(trn)
            1 -> TicketsScreen(tickets = tickets)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable fun QueueTab(trn: String, onGetQueue: () -> Unit = {}) {
    val services = listOf("Phone Service", "Internet Service")
    val branches = listOf("Portmore", "Kingston")

    var service by rememberSaveable { mutableStateOf(services[0]) }
    var branch by rememberSaveable { mutableStateOf(branches[0]) }
    var showCard by rememberSaveable { mutableStateOf(false) }
    var showAddCard by rememberSaveable { mutableStateOf(false) }

    Column() {
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
                val client = Client()
                val card = client.cards(trn)

                if (card == null) {
                    showCard = true
                }
                else {
                    toast("TODO: Proceed to queue")
                }
            },
            modifier = Modifier.scale(1.2f).align(Alignment.CenterHorizontally)) {
            Text(
                text = "Get Queue Number",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }

    if (showCard) {
        BasicAlertDialog(onDismissRequest = {}) {
            Column {
                Text("No Card found")

                TextButton(onClick = {
                    showCard = false
                    showAddCard = true
                }) {
                    Text("Add Card")
                }
            }
        }
    }

    if (showAddCard) {
        AddCard(onBack = { showAddCard = false }, trn = trn)
    }
}



@Preview
@Composable
fun QueuesPreview() {
    FlowAppTheme {
        Queues()
    }
}

@Preview
@Composable
fun TicketPreview() {
    FlowAppTheme {
        Queues(startTab = 1)
    }
}
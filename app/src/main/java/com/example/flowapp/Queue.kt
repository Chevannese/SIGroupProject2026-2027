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

@Composable
fun Queues(onBack: () -> Unit = {}, startTab: Int = 0) {
    var queueNumber by rememberSaveable { mutableStateOf<String?>(null) }
    var tab by rememberSaveable { mutableIntStateOf(startTab) }

    Column(modifier = Modifier.padding(20.dp)) {
        Spacer(Modifier.height(20.dp))
            // Tabs
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
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("My Ticket") }
                )
            }

        when (tab) {
            0 -> QueueTab()
            1 -> TicketTab()
        }
    }
}

@Composable fun QueueTab() {
    val services = listOf("Phone Service", "Internet Service")
    val branches = listOf("Portmore", "Kingston")

    var service by rememberSaveable { mutableStateOf(services[0]) }
    var branch by rememberSaveable { mutableStateOf(branches[0]) }

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

        Button(onClick = {}, modifier = Modifier.scale(1.2f).align(Alignment.CenterHorizontally)) {
            Text(
                text = "Get Queue Number",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


@Composable fun TicketTab() {
    Column(modifier = Modifier.fillMaxWidth()) {
        //val client = Client()
        //val ticket = client.getTicket()
        val ticket = Ticket.default()

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("Date:", modifier = Modifier.fillMaxWidth())
            Text(ticket.date, modifier = Modifier.fillMaxWidth())
        }
        Row(modifier = Modifier.fillMaxSize()) {
            Text("Time:", modifier = Modifier.fillMaxWidth())
            Text(ticket.time, modifier = Modifier.fillMaxWidth())
        }
        Row(modifier = Modifier.fillMaxSize()) {
            Text("Branch:", modifier = Modifier.fillMaxWidth())
            Text(ticket.branch, modifier = Modifier.fillMaxWidth())
        }
        Row(modifier = Modifier.fillMaxSize()) {
            Text("Estimated Wait Time:")
            Text(ticket.waitTime)
        }

        Row(modifier = Modifier.fillMaxSize()) {
            when (ticket.status) {
                "active" -> {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape))
                    Column {
                        Text("Active")
                        Text("You'll be notified when it's your turn")
                    }
                }
                "inactive" -> {
                    Box(modifier = Modifier.size(20.dp).clip(CircleShape))
                    Column {
                        Text("Inactive")
                        Text("Not in queue")
                    }
                }
            }
        }
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
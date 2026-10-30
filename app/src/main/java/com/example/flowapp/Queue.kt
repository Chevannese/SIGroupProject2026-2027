package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ConfirmationNumber // needs material-icons-extended
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun Queues() {
    Scaffold() { innerPadding ->
        Column(modifier = Modifier.padding(paddingValues = innerPadding).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {}) {
                    Text("Get a ticket")
                }
                Button(onClick = {}) {
                    Text("My Ticket")
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Select Service")
            Box {
                DropdownMenu(expanded = true, onDismissRequest = {}) {
                    DropdownMenuItem(text = { Text("Phone Service") }, onClick = {})
                    DropdownMenuItem(text = { Text("Phone Service") }, onClick = {})
                    DropdownMenuItem(text = { Text("Phone Service") }, onClick = {})
                    DropdownMenuItem(text = { Text("Phone Service") }, onClick = {})
                }
                }
        }
    }
}


private val Blue = Color(0xFF0A5CFF)
private val Ink = Color(0xFF1B2540)
private val PaleBlue = Color(0xFFDCEBFB)
private val FieldFill = Color(0xFFE9EDF8)
private val TicketFill = Color(0xFFD9E2F7)
private val ScreenBg = Color(0xFFF8FAFF)

private enum class Tab(val label: String) {
    GetTicket("Get a Ticket"),
    MyTicket("My Ticket")
}

@Composable
fun QueueSystemScreen(onBack: () -> Unit = {}) {
    val services = listOf("Customer Service", "Cashier", "Account Opening", "Loans")
    val branches = listOf("Spanish Town", "Portmore", "Kingston", "Mandeville")

    var tab by rememberSaveable { mutableStateOf(Tab.GetTicket) }
    var service by rememberSaveable { mutableStateOf(services.first()) }
    var branch by rememberSaveable { mutableStateOf(branches.first()) }
    var queueNumber by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        TitleBar(onBack = onBack)

        Spacer(Modifier.height(20.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Tab.entries.forEach { t ->
                val selected = t == tab
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (selected) Blue else PaleBlue)
                        .clickable { tab = t }
                        .padding(horizontal = 28.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t.label,
                        color = if (selected) Color.White else Blue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        when (tab) {
            Tab.GetTicket -> {
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

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(Blue)
                        .clickable {
                            // Replace with a real call to your backend
                            queueNumber = "A${Random.nextInt(100, 999)}"
                        }
                        .padding(horizontal = 40.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Get Queue Number",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                queueNumber?.let {
                    Spacer(Modifier.height(24.dp))
                    TicketCard(number = it)
                }
            }

            Tab.MyTicket -> {
                val number = queueNumber
                if (number != null) {
                    TicketCard(number = number)
                } else {
                    Text(
                        text = "You don't have a ticket yet. Go to Get a Ticket to join the queue.",
                        color = Ink,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PaleBlue)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = Ink,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = "Queue System",
            color = Ink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(FieldFill)
                    .clickable { expanded = true }
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected,
                    color = Ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Ink,
                    modifier = Modifier.size(28.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketCard(number: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TicketFill)
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.ConfirmationNumber,
            contentDescription = null,
            tint = Ink,
            modifier = Modifier.size(52.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "Your Queue Number",
                color = Ink,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = number,
                color = Ink,
                fontWeight = FontWeight.Medium,
                fontSize = 30.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun QueueSystemScreenPreview() {
    MaterialTheme {
        QueueSystemScreen()
    }
}


@Preview
@Composable
fun QueuesPreview() {
    MaterialTheme {
        Queues()
    }
}

@Preview
@Composable
fun QueuesPreview2() {
    MaterialTheme {
        QueueSystemScreen()
    }
}
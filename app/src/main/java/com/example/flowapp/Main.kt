package com.example.flowapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(onProfileClick: () -> Unit = {}, onLogoutClick: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var showProfile by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Flow App")
                },
                actions = {
                    IconButton(onClick = { showProfile = true }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Button(onClick = { }) {
                        Text("Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = { Icon( imageVector = Icons.Default.Home, contentDescription = "Appointments") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },
                    icon = { Icon( imageVector = Icons.Default.Queue, contentDescription = "Appointments") },
                    label = { Text("Queue") }
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },
                    icon = { Icon( imageVector = Icons.Default.CalendarMonth, contentDescription = "Appointments") },
                    label = { Text("Appointment") }
                )
                NavigationBarItem(
                    selected = tab == 3,
                    onClick = { tab = 3 },
                    icon = { Icon( imageVector = Icons.Default.Wallet, contentDescription = "Payments") },
                    label = { Text("Payments") }
                )
            }
        }
    ) { innerPadding ->
        Box (modifier = Modifier.padding(innerPadding)) {
            when (tab) {
                1 -> Home()
                2 -> Queues()
                3 -> Appointments()
                4 -> Payments()
            }
        }
    }

    if (showProfile) {
        Profile({}, sheetState)
    }
}

@Composable
fun Home() {
    Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun Queues() {
    Text(text = "Queues Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun Appointments() {
    Text(text = "Appointments Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun Payments() {
    Text(text = "Payments Screen", style = MaterialTheme.typography.headlineMedium)
}

@Preview
@Composable fun MainPreview() {
    FlowAppTheme()  {
        Main({}, {})
    }
}

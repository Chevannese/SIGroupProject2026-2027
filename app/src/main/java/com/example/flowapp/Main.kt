package com.example.flowapp

import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(onLogoutClick: () -> Unit, trn: String) {
    var tab by remember { mutableIntStateOf(0) }
    var showProfile by remember { mutableStateOf(false) }
    var showDetailedProfile by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val client = Client()
    val user = client.user(trn)

    Log.d("USERINFO", user.toString())
    var name = "Unknown"

    if (user != null) {
        name = user.name
    }

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
                    icon = { Icon( imageVector = Icons.Default.Wallet, contentDescription = "Payments") },
                    label = { Text("Payments") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (tab) {
                0 -> Home()
                1 -> Queues()
                2 -> Payments()
            }
        }

        if (showProfile) {
            Profile(
                onBack = { showProfile = false },
                onLogoutClick = onLogoutClick,
                onDetails = { showProfile = false; showDetailedProfile = true },
                name = name,
                sheetState = sheetState
            )
        }
    }


    if (showDetailedProfile) {
        Personal(onBack = { showDetailedProfile = false }, trn = trn)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(onBack: () -> Unit = {}, onLogoutClick: () -> Unit = {}, onDetails: () -> Unit = {}, name: String = "", sheetState: SheetState = rememberModalBottomSheetState()) {
    ModalBottomSheet(onDismissRequest = onBack, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.Person, modifier = Modifier.size(100.dp), contentDescription = "icon")
            Spacer(modifier = Modifier.height(20.dp))
            // display name
            Text(text = name, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onDetails) {
                    Text("More Details")
                }

                Button(onClick = onLogoutClick) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun Home() {
    Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
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
        Main({}, "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable fun ProfilePreview() {
    FlowAppTheme()  {
        Profile()
    }
}


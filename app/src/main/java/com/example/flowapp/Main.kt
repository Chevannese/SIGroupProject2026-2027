package com.example.flowapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.flowapp.ui.theme.FlowAppTheme

enum class FlowTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    QUEUES("Queues", Icons.Default.List),
    APPOINTMENTS("Appointments", Icons.Default.DateRange),
    PAYMENTS("Payments", Icons.Default.ShoppingCart)
}

@Composable
fun Main() {
    var selectedTabItem by remember { mutableIntStateOf(0) }
    val tabs = FlowTab.entries

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabItem == index,
                        onClick = { selectedTabItem = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (tabs[selectedTabItem]) {
                FlowTab.HOME -> HomeScreen()
                FlowTab.QUEUES -> QueuesScreen()
                FlowTab.APPOINTMENTS -> AppointmentsScreen()
                FlowTab.PAYMENTS -> PaymentsScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun QueuesScreen() {
    Text(text = "Queues Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun AppointmentsScreen() {
    Text(text = "Appointments Screen", style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun PaymentsScreen() {
    Text(text = "Payments Screen", style = MaterialTheme.typography.headlineMedium)
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    FlowAppTheme {
        Main()
    }
}

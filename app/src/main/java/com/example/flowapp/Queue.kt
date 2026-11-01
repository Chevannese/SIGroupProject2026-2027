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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.ConfirmationNumber // needs material-icons-extended
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme

private val Blue = Color(0xFF0A5CFF)

private val ScreenBg = Color(0xFFF8FAFF)

@Composable
fun Queues(onBack: () -> Unit = {}) {
    val services = listOf("Customer Service", "Cashier", "Account Opening", "Loans")
    val branches = listOf("Spanish Town", "Portmore", "Kingston")

    var service by rememberSaveable { mutableStateOf(services[0]) }
    var branch by rememberSaveable { mutableStateOf(branches[0]) }
    var queueNumber by rememberSaveable { mutableStateOf<String?>(null) }
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(ScreenBg).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Spacer(Modifier.height(20.dp))

        // Tabs
        SecondaryTabRow(selectedTabIndex = tab, modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Blue)) {
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



@Preview
@Composable
fun QueuesPreview() {
    FlowAppTheme {
        Queues()
    }
}
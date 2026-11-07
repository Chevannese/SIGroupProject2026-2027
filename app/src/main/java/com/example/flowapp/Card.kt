package com.example.flowapp

import android.widget.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme

@Composable
fun AddCard(onBack: () -> Unit = {}, trn: String) {
    var ccNumber by remember { mutableStateOf("") }
    var expDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showCalendar = false

    var validCardNumber by remember { mutableStateOf(false) }

    BackBar(
        title = "Card Details",
        onBack = onBack,
        content = {
            Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(20.dp)) {
                Row(
                    Modifier.fillMaxWidth().border(2.dp, Color.Blue, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = "Add Card",
                            Modifier.size(50.dp)
                        )
                        Text("Card")
                    }
                    Checkbox(checked = true, onCheckedChange = { },)
                }
                Spacer(Modifier.height(20.dp))

                Text("Saved Card", Modifier.fillMaxSize())
                OutlinedTextField(
                    "",
                    {},
                    leadingIcon = { Icon(Icons.Default.CreditCard, "cc") },
                    modifier = Modifier.fillMaxSize()
                )
                Spacer(Modifier.height(20.dp))
                Text("Card Number", modifier = Modifier.fillMaxSize())
                OutlinedTextField(
                    ccNumber,
                    {
                        ccNumber = it
                        validCardNumber = ccNumber.all { it.isDigit() } && ccNumber.length == 16
                    },
                    placeholder = { Text("0000 0000 0000 0000") },
                    modifier = Modifier.fillMaxSize(),
                    supportingText = {
                        if (!validCardNumber) {
                            Text("Invalid card number", color = Color.Red)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column {
                        Text("Expiry Date")
                        OutlinedTextField(
                            value = "",
                            onValueChange = { expDate = it },
                            leadingIcon = {
                                IconButton(onClick = { showCalendar = !showCalendar }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Select date"
                                    )
                                }
                            }
                        )
                    }
                    Column {
                        Text("CVV")
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it },
                            leadingIcon = {
                                IconButton(onClick = { }) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Select date"
                                    )
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        val client = Client()
                        val response = client.add_card(trn, ccNumber)

                        when (response) {
                            false -> toast("Error adding card")
                            true -> {
                                toast("Card added")
                                onBack()
                            }
                        }
                    },
                    Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
                ) {
                    Text("Add Card")
                }
            }
        }
    )
}

@Preview
@Composable
fun CardPreview() {
    FlowAppTheme {
        AddCard(trn = "123456789")
    }
}
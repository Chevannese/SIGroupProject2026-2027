package com.example.flowapp

import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RatingBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlin.math.roundToInt


@Composable
fun Home(trn: String, onChangeTab: (Int) -> Unit) {
    var showCardDialog by remember { mutableStateOf(false) }
    var savedCard by remember { mutableStateOf<String?>(null) }
    val scroll = rememberScrollState()

    val customerCard = remember(trn) {
        try {
            Client().getAllCards().find { it.trn == trn }
        }
        catch (e: Exception) {
            null
        }
    }
    val balanceText = customerCard?.let { "$%.2f".format(it.balance) } ?: "$0.00"

    Box {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Account Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = balanceText,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    IconButton(
                        onClick = {
                            MainActivity.instance.setContentView(R.layout.account_balance_page)
                            val back = MainActivity.instance.findViewById<Button>(R.layout.account_balance_back_button)

                            back.setOnClickListener {
                                MainActivity.instance.showMain(trn)
                            }

                        }
                    ) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Go to account balance page")
                    }
                }
            }

            HomeButton(
                "Join the queue",
                "Get a ticket for a branch or service",
                Icons.Default.People,
                Color(132, 180, 243, 255),
                onClick = {}
            )

            HomeButton(
                "Make a payment",
                "Pay your bill easily and securely",
                Icons.Default.CalendarMonth,
                Color(245, 245, 165, 255),
                onClick = { onChangeTab(2) }
            )

            HomeButton(
                "View Services",
                "Explore all available services",
                Icons.Default.HomeRepairService,
                Color(217, 165, 245, 255),
                onClick = {
                    MainActivity.instance.setContentView(R.layout.service)
                    val back = MainActivity.instance.findViewById<ImageButton>(R.id.service_back_button)

                    back.setOnClickListener {
                        MainActivity.instance.showMain(trn)
                    }
                }
            )

            HomeButton(
                "Give Feedback",
                "Share your thoughts and problems with us",
                Icons.Default.Feedback,
                Color(245, 165, 165, 255),
                {
                    MainActivity.instance.setContentView(R.layout.feedback)
                    val submit = MainActivity.instance.findViewById<Button>(R.id.feedback_submit_btn)
                    val feedback = MainActivity.instance.findViewById<EditText>(R.id.enter_feedback)
                    val rating = MainActivity.instance.findViewById<RatingBar>(R.id.rating)
                    val back = MainActivity.instance.findViewById<ImageButton>(R.id.feedback_back_button)

                    back.setOnClickListener {
                        MainActivity.instance.showMain(trn)
                    }

                    submit.setOnClickListener {
                        val text = feedback.text.toString()
                        val ratingValue = rating.rating.roundToInt()

                        // TODO: Actually send feedback
                        toast("Feedback submission not implemented yet\nHowever here is the rating entered $ratingValue")
                        MainActivity.instance.showMain(trn)
                    }
                }
            )
        }

        //Box(modifier = Modifier.size(5.dp, 20.dp).align(Alignment.End))
    }


    if (showCardDialog) {
        if (!savedCard.isNullOrBlank()) {
            AlertDialog(
                onDismissRequest = { showCardDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Saved Card",
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = { Text("Saved Credit Card") },
                text = {
                    Column {
                        Text(
                            text = "Card Number:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = savedCard!!.substring(savedCard!!.length - 4),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { showCardDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
        else {
            AddCard(
                onBack = {
                    //savedCard = client.cards(trn)
                    showCardDialog = false
                },
                trn = trn
            )
        }
    }
}

@Composable fun HomeButton(title: String, text: String, icon: ImageVector, colour: Color = Color.Black, onClick: () -> Unit) {
    Spacer(modifier = Modifier.size(20.dp))
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = colour.copy(alpha = 0.2f)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
            Column {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp).background(colour, CircleShape).padding(10.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.titleLarge)
                        Text(text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Box(modifier = Modifier.padding(10.dp).align(Alignment.End).background(Color(0.0f, 0.0f, 0.0f, 0.2f),
                    CircleShape
                )) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp).background(colour, CircleShape).padding(10.dp)
                    )
                }
            }
        }
}

// preview home
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    FlowAppTheme(darkTheme = true) {
        Home(trn = "123456789", {})
    }
}

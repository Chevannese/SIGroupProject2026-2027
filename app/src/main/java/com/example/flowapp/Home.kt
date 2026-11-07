package com.example.flowapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme

@Composable
fun Home() {
    Column {
        Row {
            Box(modifier = Modifier.size(20.dp)) {
                Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
            }
            Box(modifier = Modifier.size(20.dp)) {
                Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
            }
        }
        Row {
            Box(modifier = Modifier.size(20.dp)) {
                Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
            }
            Box(modifier = Modifier.size(20.dp)) {
                Text(text = "Home Screen", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

// preview home
@Preview
@Composable
fun HomePreview() {
    FlowAppTheme {
        Home()
    }
}
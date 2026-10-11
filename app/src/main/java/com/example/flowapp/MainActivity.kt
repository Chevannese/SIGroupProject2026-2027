package com.example.flowapp

import android.os.Bundle
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // shows login when the app starts
        showLogin()
    }

    private fun showLogin() {
        // switch to login page
        setContentView(R.layout.login)

        // yoink register link button
        val register: TextView = findViewById(R.id.registerBtn)

        // add event listener to the button
        register.setOnClickListener {
            showRegister()
        }
    }

    private fun showRegister() {
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // switch to register page
                    Register(
                        modifier = Modifier.padding(innerPadding),
                        // switches back to login page
                        showLogin = { showLogin() }
                    )
                }
            }
        }
    }
}

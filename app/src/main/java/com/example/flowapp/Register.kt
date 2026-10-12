package com.example.flowapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*

@Composable
fun Register(
    modifier: Modifier = Modifier,
    register: () -> Unit = {},
    showLogin: () -> Unit = {}
) {
    // variables to store user input
    // the UI autoupdates these with the parts that say onValueChange
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val blue = colorResource(id = R.color.dark_blue)
    val darkBlue = colorResource(id = R.color.dark_blue)
    val darkGray = colorResource(id = R.color.dark_gray)
    val white = colorResource(id = R.color.white)

    // this is basicallt the same as LinearLayout from the login.xml
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(95.dp))

        // App title & system subtitle
        Text(
            text = "Flow",
            fontSize = 50.sp,
            color = blue,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Real Time Scheduling System",
            color = blue,
            fontWeight = FontWeight.Bold
        )

        // Screen header
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = "Create Account",
            color = darkBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // First Name
        OutlinedTextField(
            value = name,
            onValueChange = { text: String -> name = text },
            placeholder = { Text(text = "Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { text -> email = text },
            placeholder = { Text(text = "Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Phone
        OutlinedTextField(
            value = phone,
            onValueChange = { text -> phone = text },
            placeholder = { Text(text = "Phone Number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = { text -> password = text },
            placeholder = { Text(text = "Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(15.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { text -> confirmPassword = text },
            placeholder = { Text(text = "Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Register Button
        // runs register function when clicked
        Button(onClick = register) {
            Text(
                text = "Register",
                color = white,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "______________   OR   ______________")

        Spacer(modifier = Modifier.height(15.dp))

        // Already have an account? Login Link
        TextButton(onClick = showLogin) {
            Text(
                text = "Don't Have an Account? Login Here",
                color = darkBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}
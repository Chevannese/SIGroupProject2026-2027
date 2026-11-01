package com.example.flowapp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.flowapp.ui.theme.FlowAppTheme

@Composable
fun Register(
    modifier: Modifier = Modifier,
    register: () -> Unit = {},
    showLogin: () -> Unit = {},
) {
    // variables to store user input
    // the UI autoupdates these with the parts that say onValueChange
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val blue = colorResource(id = R.color.blue)
    val darkBlue = colorResource(id = R.color.dark_blue)
    val white = colorResource(id = R.color.white)

    val passwordVisualTransformation = remember { PasswordVisualTransformation() }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App title & system subtitle
            Text(
                text = "Flow",
                fontSize = 50.sp,
                color = blue,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Real Time Scheduling System",
                color = blue,
                fontWeight = FontWeight.Bold,
            )

            // Screen header
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = "Create Account",
                color = darkBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // First Name
            RegisterInputField(
                value = name,
                onValueChange = { name = it },
                placeholderText = "Name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Email
            RegisterInputField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Phone
            RegisterInputField(
                value = phone,
                onValueChange = { phone = it },
                placeholderText = "Phone Number",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Password
            RegisterInputField(
                value = password,
                onValueChange = { password = it },
                placeholderText = "Password",
                visualTransformation = passwordVisualTransformation,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Confirm Password
            RegisterInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholderText = "Confirm Password",
                visualTransformation = passwordVisualTransformation,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Register Button
            // runs register function when clicked
            Button(onClick = register) {
                Text(
                    text = "Register",
                    color = white,
                    fontWeight = FontWeight.Bold,
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
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RegisterInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholderText) },
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}

@Preview
@Composable fun RegisterPreview() {
    FlowAppTheme {
        Register()
    }
}
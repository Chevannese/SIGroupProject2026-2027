package com.example.flowapp

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flowapp.ui.theme.FlowAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileItem(icon: ImageVector, title: String, value: String, menu: Boolean = false, onClick: () -> Unit) {
    val lightBlueIconBg = Color(0xFFEAF2FF)
    val darkBlue = Color(0xFF0D3B72)
    val primaryBlue = Color(0xFF1976D2)
    val grayText = Color(0xFF757575)

    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        // Rounded Icon Circle
        Box(modifier = Modifier.size(52.dp).background(color = lightBlueIconBg, shape = CircleShape), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = primaryBlue, modifier = Modifier.size(26.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title and Value
        Column(modifier = Modifier.weight(1f)) {
            Text(title)
            Text(value)
        }

        if (menu) {
            // Chevron Right
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Personal(onBack: () -> Unit = {}, trn: String) {
    var changePassword by remember { mutableStateOf(false) }
    var showAddCard by remember { mutableStateOf(false) }
    val client = Client()
    var user = client.user(trn)
    val savedCard = client.cards(trn)
    var paymentMethodClick = {}

    if (savedCard == null) {
        paymentMethodClick = { showAddCard = true }
    }
    if (user == null) {
        user = User.default()
    }

    BackBar("Profile", onBack) {
        // Personal Information Header Box

        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),) {

            ProfileItem(
                icon = Icons.Default.Person,
                title = "Name",
                value = user.name,
                onClick = {}
            )

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(start = 72.dp)
            )

            ProfileItem(
                icon = Icons.Default.Receipt,
                title = "Email",
                value = user.email,
                onClick = {}
            )

            ProfileItem(
                icon = Icons.Default.Phone,
                title = "Phone",
                value = user.phone,
                onClick = {}
            )

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(start = 72.dp)
            )

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(start = 72.dp)
            )

            ProfileItem(
                icon = Icons.Default.Lock,
                title = "Password",
                value = "********",
                menu = true,
                onClick = { changePassword = true }
            )

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(start = 72.dp)
            )
                
            ProfileItem(
                icon = Icons.Default.Payments,
                title = "Purchase History",
                value = "View past purchases",
                onClick = {}
            )

            HorizontalDivider(
                color = Color(0xFFF0F0F0),
                modifier = Modifier.padding(start = 72.dp)
            )

            ProfileItem(
                icon = Icons.Default.CreditCard,
                title = "Payment Method",
                value = savedCard?.takeLast(4)?.let { "Visa Card **** $it" } ?: "No card saved",
                onClick = paymentMethodClick
            )
        }
    }

    if (changePassword) {
        ChangePassword( onBack = { changePassword = false }, trn)
    }
}

suspend fun getUser(trn: String): User = withContext(Dispatchers.IO) {
    val client = Client()
    val temp = client.user(trn)
    when (temp) {
        null -> return@withContext User.default()
        else -> return@withContext temp
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ChangePassword(onBack: () -> Unit = { }, trn: String) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var password8Chars by remember { mutableStateOf(false) }
    var passwordsMatch by remember { mutableStateOf(false) }
    var passwordContainsLNS by remember { mutableStateOf(false) }
    var enableButton by remember { mutableStateOf(true) }

    BackBar(title = "Change Password", onBack) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { text -> currentPassword = text },
                placeholder = { Text("Old Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    text -> newPassword = text
                    passwordsMatch = confirmPassword == newPassword
                    password8Chars = newPassword.length >= 8
                    passwordContainsLNS = newPassword.any { it.isLetter() } && newPassword.any { it.isDigit() } && newPassword.any { it == '!' || it == '@' || it == '#' || it == '$' || it == '%' || it == '^' || it == '&' || it == '*' }
                },
                placeholder = { Text(text = "New Password") },
                supportingText = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(password8Chars, {}, enabled = false)
                            Text("Password must be at least 8 characters long",)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(passwordContainsLNS, {}, enabled = false)
                            Text("Password must contain letters, numbers and a special character")
                        }
                    }
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
            )

            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = {
                    text -> confirmPassword = text
                    passwordsMatch = confirmPassword == newPassword
                },
                placeholder = { Text(text = "Confirm Password") },
                supportingText = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = passwordsMatch, onCheckedChange = {}, enabled = false)
                        Text("Passwords must match")
                    }
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                enabled = enableButton,
                onClick = {
                    enableButton = false
                    val client = Client()
                    val response = client.changePassword(trn, currentPassword, newPassword, confirmPassword)

                    when (response) {
                        ChangeOldPassword.EMPTY_FIELD -> Toast.makeText(MainActivity.instance.applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        ChangeOldPassword.PASSWORD_SHORT -> Toast.makeText(MainActivity.instance.applicationContext, "Password should not be less than 8 characters", Toast.LENGTH_SHORT).show()
                        ChangeOldPassword.PASSWORD_LONG -> Toast.makeText(MainActivity.instance.applicationContext, "Password should not be greater than 30 characters", Toast.LENGTH_SHORT).show()
                        ChangeOldPassword.SAME_PASSWORD -> Toast.makeText(MainActivity.instance.applicationContext, "Old password cannot be your new password", Toast.LENGTH_SHORT).show()
                        ChangeOldPassword.INVALID_PASSWORD -> Toast.makeText(MainActivity.instance.applicationContext, "Invalid password\nPassword must contain letters, numbers and a special character", Toast.LENGTH_SHORT).show()
                        ChangeOldPassword.PASSWORD_MISMATCH -> Toast.makeText(MainActivity.instance.applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        ChangeOldPassword.OK -> {
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                        ChangeOldPassword.ERROR -> Toast.makeText(MainActivity.instance.applicationContext, "An error occurred", Toast.LENGTH_SHORT).show()
                        else -> {}
                    }

                    enableButton = true
                }
            ) {
                Text("Change Password")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalPreview() {
    FlowAppTheme {
        Personal(trn = "")
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    FlowAppTheme {
        ChangePassword(trn = "")
    }
}
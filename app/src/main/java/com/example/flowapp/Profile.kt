package com.example.flowapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flowapp.ui.theme.FlowAppTheme

@Composable
fun ProfileItem(
    icon: ImageVector,
    title: String,
    value: String,
    menu: Boolean = false,
    onClick: () -> Unit
) {
    val lightBlueIconBg = Color(0xFFEAF2FF)
    val darkBlue = Color(0xFF0D3B72)
    val primaryBlue = Color(0xFF1976D2)
    val grayText = Color(0xFF757575)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rounded Icon Circle
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(color = lightBlueIconBg, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = primaryBlue,
                modifier = Modifier.size(26.dp)
            )
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
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = darkBlue,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(onBack: () -> Unit = {}) {
    val lightBlue = Color(0xFFE8F1FC)
    val darkBlue = Color(0xFF0D3B72)
    var changePassword by remember { mutableStateOf(false) }

    BackBar(
        "Profile",
        onBack,
        { innerPadding ->
            // Personal Information Header Box
            Box(modifier = Modifier.padding(innerPadding)) {
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    ProfileItem(
                        icon = Icons.Default.Person,
                        title = "Name",
                        value = "Kimoya Kimoya",
                        onClick = {}
                    )

                    HorizontalDivider(
                        color = Color(0xFFF0F0F0),
                        modifier = Modifier.padding(start = 72.dp)
                    )

                    ProfileItem(
                        icon = Icons.Default.Email,
                        title = "Email",
                        value = "kimoya@example.com",
                        onClick = {}
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
                        onClick = {}
                    )

                    HorizontalDivider(
                        color = Color(0xFFF0F0F0),
                        modifier = Modifier.padding(start = 72.dp)
                    )

                    ProfileItem(
                        icon = Icons.Default.LocationOn,
                        title = "Address",
                        value = "Kingston, Jamaica",
                        onClick = {}
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
                        value = "Visa Card **** 2341",
                        onClick = {}
                    )
                }
            }
        }
    )

    if (changePassword) {
        ChangePassword( onBack = { changePassword = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ChangePassword(onBack: () -> Unit = { }) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var hasspecialchar by remember { mutableStateOf(false) }
    var has8letters by remember { mutableStateOf(false) }
    var hasnumber by remember { mutableStateOf(false) }
    val checkPassword = {
        has8letters = currentPassword.length >= 8
        hasnumber = currentPassword.any { it.isDigit() }
        hasspecialchar = currentPassword.any { !it.isLetterOrDigit() }
    }

    BackBar(
        title = "Change Password",
        onBack,
        content = { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { text -> currentPassword = text },
                    placeholder = { Text(text = "Old Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { text -> newPassword = text },
                    placeholder = { Text(text = "New Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                )
                Spacer(modifier = Modifier.height(15.dp))
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { text -> confirmPassword = text },
                    placeholder = { Text(text = "Confirm Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    FlowAppTheme {
        Profile()
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    FlowAppTheme {
        ChangePassword()
    }
}
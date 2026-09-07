package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppTextField
import com.example.ui.components.GaneshaIconBadge
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    festivalName: String = "Ganesh Chaturthi Festival 2026",
    organizationName: String = "SHREE\nSIDDHIVINAYAKA\nPARIVAR",
    onLoginClick: (username: String, password: String, onError: (String) -> Unit) -> Unit = { _, _, _ -> },
    onLoginSuccess: () -> Unit = {}
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("screen_login")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Logo and festival title
            GaneshaIconBadge(
                size = 84.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = organizationName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B156E),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = festivalName,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Welcome Back",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Please login to continue",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Inputs
            AppTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = null
                },
                label = "Mobile Number / Username",
                placeholder = "Enter mobile number or admin",
                testTag = "input_username"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    placeholder = { Text("Enter password", fontSize = 13.sp, color = TextSecondary) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password",
                                tint = TextSecondary
                            )
                        }
                    },
                    isError = errorMessage != null,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalPurplePrimary,
                        unfocusedBorderColor = BorderLight,
                        errorBorderColor = ExpenseRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password")
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    fontSize = 12.sp,
                    color = ExpenseRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Remember me and Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = RoyalPurplePrimary,
                            uncheckedColor = BorderLight
                        ),
                        modifier = Modifier.testTag("cb_remember_me")
                    )
                    Text(
                        text = "Remember me",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "Forgot Password?",
                    fontSize = 12.sp,
                    color = RoyalPurplePrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { showForgotPasswordDialog = true }
                        .testTag("btn_forgot_password")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = {
                    if (username.isBlank()) {
                        errorMessage = "Please enter username or mobile number"
                        return@Button
                    }
                    onLoginClick(username.trim(), password) { err ->
                        errorMessage = err
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_login")
            ) {
                Text(
                    text = "LOGIN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(36.dp))

            Row(
                modifier = Modifier.padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Single-Admin Mode • ",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Head of Committee",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = RoyalPurplePrimary
                )
            }
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Admin Account Recovery", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "This app runs in local offline-secure mode. If you have not changed your default password, you can login with:\n\n• Username: admin\n• Default Password: admin\n\nOnce logged in, you can update your password under More > Change Password.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TextPrimary
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("OK", color = RoyalPurplePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

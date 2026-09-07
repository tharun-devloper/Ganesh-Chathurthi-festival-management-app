package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CollectionGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onSubmitChangePassword: (currentPass: String, newPass: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPassVisible by remember { mutableStateOf(false) }
    var newPassVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    var currentPassError by remember { mutableStateOf<String?>(null) }
    var newPassError by remember { mutableStateOf<String?>(null) }
    var confirmPassError by remember { mutableStateOf<String?>(null) }

    var successMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_change_password")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Change Password",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = RoyalPurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Changing your password does NOT affect or reset any donation, expense, or receipt records. Passwords are saved with SHA-256 device salt encryption.",
                            fontSize = 12.sp,
                            color = Color(0xFF4A154B),
                            lineHeight = 16.sp
                        )
                    }
                }

                // Form
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Update Admin Credentials",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        // Current Password
                        Column {
                            Text(
                                text = "Current Password *",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = currentPassword,
                                onValueChange = {
                                    currentPassword = it
                                    currentPassError = null
                                },
                                placeholder = { Text("Enter current password", fontSize = 13.sp, color = TextSecondary) },
                                visualTransformation = if (currentPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { currentPassVisible = !currentPassVisible }) {
                                        Icon(
                                            imageVector = if (currentPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                isError = currentPassError != null,
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalPurplePrimary,
                                    unfocusedBorderColor = BorderLight,
                                    errorBorderColor = ExpenseRed
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_current_password")
                            )
                            if (currentPassError != null) {
                                Text(
                                    text = currentPassError ?: "",
                                    fontSize = 11.sp,
                                    color = ExpenseRed,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // New Password
                        Column {
                            Text(
                                text = "New Password *",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = {
                                    newPassword = it
                                    newPassError = null
                                },
                                placeholder = { Text("Enter new password (min 4 characters)", fontSize = 13.sp, color = TextSecondary) },
                                visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                        Icon(
                                            imageVector = if (newPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                isError = newPassError != null,
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalPurplePrimary,
                                    unfocusedBorderColor = BorderLight,
                                    errorBorderColor = ExpenseRed
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_new_password")
                            )
                            if (newPassError != null) {
                                Text(
                                    text = newPassError ?: "",
                                    fontSize = 11.sp,
                                    color = ExpenseRed,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }

                        // Confirm New Password
                        Column {
                            Text(
                                text = "Confirm New Password *",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    confirmPassError = null
                                },
                                placeholder = { Text("Re-enter new password", fontSize = 13.sp, color = TextSecondary) },
                                visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                trailingIcon = {
                                    IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                                        Icon(
                                            imageVector = if (confirmPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                isError = confirmPassError != null,
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalPurplePrimary,
                                    unfocusedBorderColor = BorderLight,
                                    errorBorderColor = ExpenseRed
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_confirm_password")
                            )
                            if (confirmPassError != null) {
                                Text(
                                    text = confirmPassError ?: "",
                                    fontSize = 11.sp,
                                    color = ExpenseRed,
                                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        var hasError = false
                        if (currentPassword.isBlank()) {
                            currentPassError = "Please enter current password"
                            hasError = true
                        }
                        if (newPassword.isBlank()) {
                            newPassError = "New password cannot be empty"
                            hasError = true
                        } else if (newPassword.length < 4) {
                            newPassError = "Password must be at least 4 characters"
                            hasError = true
                        }
                        if (confirmPassword != newPassword) {
                            confirmPassError = "Passwords do not match"
                            hasError = true
                        }

                        if (!hasError) {
                            onSubmitChangePassword(currentPassword, newPassword) { success, message ->
                                if (success) {
                                    successMessage = "Password changed successfully! Keep your new password secure."
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                } else {
                                    currentPassError = message
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_submit_change_password")
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UPDATE PASSWORD",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Success Dialog
        successMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = {
                    successMessage = null
                    onBack()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CollectionGreen,
                        modifier = Modifier.size(40.dp)
                    )
                },
                title = {
                    Text("Password Updated", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Text(msg, fontSize = 13.sp, color = TextPrimary)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            successMessage = null
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary)
                    ) {
                        Text("DONE", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdminSecurity
import com.example.ui.components.AppHeader
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CollectionGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MoreMenuScreen(
    adminSecurity: AdminSecurity = AdminSecurity(),
    onNavigateToFestivalSettings: () -> Unit = {},
    onNavigateToUserManagement: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToActivityHistory: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToBackupRestore: () -> Unit = {},
    onConfigureAppLock: (enabled: Boolean, pin: String, timeoutMinutes: Int, isBiometric: Boolean) -> Unit = { _, _, _, _ -> },
    onLogoutClick: () -> Unit,
    onClearDataClick: () -> Unit = {},
    onShowMessage: (String) -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showAppLockConfigDialog by remember { mutableStateOf(false) }

    // State for App Lock config dialog
    var appLockPinInput by remember { mutableStateOf("") }
    var selectedTimeoutMinutes by remember { mutableIntStateOf(adminSecurity.autoLockTimeoutMinutes) }
    var pinError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .testTag("screen_more_menu")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "More"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Admin Profile Header Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToUserManagement() }
                        .testTag("card_admin_profile")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(RoyalPurplePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = adminSecurity.adminName.take(1).uppercase().ifBlank { "A" },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = adminSecurity.adminName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = adminSecurity.mobileNumber,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFEDE9FE))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Head of Committee",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurplePrimary
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open Profile",
                            tint = TextSecondary
                        )
                    }
                }

                // General Section
                Column {
                    Text(
                        text = "General",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    ) {
                        Column {
                            MenuItemRow(
                                title = "Festival Settings",
                                icon = Icons.Default.Settings,
                                testTag = "menu_item_festival_settings",
                                onClick = onNavigateToFestivalSettings
                            )
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)
                            MenuItemRow(
                                title = "User Management",
                                icon = Icons.Default.ManageAccounts,
                                testTag = "menu_item_user_management",
                                onClick = onNavigateToUserManagement
                            )
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)
                            MenuItemRow(
                                title = "Categories",
                                icon = Icons.Default.Category,
                                testTag = "menu_item_categories",
                                onClick = onNavigateToCategories
                            )
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)
                            MenuItemRow(
                                title = "Backup & Restore",
                                icon = Icons.Default.CloudDownload,
                                testTag = "menu_item_backup_restore",
                                onClick = onNavigateToBackupRestore
                            )
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)
                            MenuItemRow(
                                title = "Activity History",
                                icon = Icons.Default.History,
                                testTag = "menu_item_activity_history",
                                onClick = onNavigateToActivityHistory
                            )
                        }
                    }
                }

                // Account & Security Section
                Column {
                    Text(
                        text = "Account & Security",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                    ) {
                        Column {
                            MenuItemRow(
                                title = "Change Password",
                                icon = Icons.Default.LockReset,
                                testTag = "menu_item_change_password",
                                onClick = onNavigateToChangePassword
                            )
                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)

                            // App Lock with Toggle Switch & Configure
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (adminSecurity.isAppLockEnabled) {
                                            // Toggle off
                                            onConfigureAppLock(false, "", adminSecurity.autoLockTimeoutMinutes, false)
                                        } else {
                                            // Open configuration dialog
                                            selectedTimeoutMinutes = adminSecurity.autoLockTimeoutMinutes
                                            appLockPinInput = ""
                                            pinError = null
                                            showAppLockConfigDialog = true
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .testTag("menu_item_app_lock"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "App Lock",
                                    tint = if (adminSecurity.isAppLockEnabled) RoyalPurplePrimary else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "App Lock",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    val timeoutText = when (adminSecurity.autoLockTimeoutMinutes) {
                                        0 -> "Immediately"
                                        1 -> "After 1 min"
                                        5 -> "After 5 min"
                                        15 -> "After 15 min"
                                        else -> "Immediate"
                                    }
                                    Text(
                                        text = if (adminSecurity.isAppLockEnabled) "Enabled ($timeoutText)" else "Disabled",
                                        fontSize = 11.sp,
                                        color = if (adminSecurity.isAppLockEnabled) CollectionGreen else TextSecondary
                                    )
                                }

                                if (adminSecurity.isAppLockEnabled) {
                                    IconButton(
                                        onClick = {
                                            selectedTimeoutMinutes = adminSecurity.autoLockTimeoutMinutes
                                            appLockPinInput = ""
                                            pinError = null
                                            showAppLockConfigDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = "Configure Lock",
                                            tint = RoyalPurplePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Switch(
                                    checked = adminSecurity.isAppLockEnabled,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedTimeoutMinutes = adminSecurity.autoLockTimeoutMinutes
                                            appLockPinInput = ""
                                            pinError = null
                                            showAppLockConfigDialog = true
                                        } else {
                                            onConfigureAppLock(false, "", adminSecurity.autoLockTimeoutMinutes, false)
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = RoyalPurplePrimary
                                    )
                                )
                            }

                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)

                            MenuItemRow(
                                title = "Clear All Local Data",
                                icon = Icons.Default.DeleteSweep,
                                titleColor = ExpenseRed,
                                iconColor = ExpenseRed,
                                testTag = "menu_item_clear_data",
                                onClick = { showClearDataDialog = true }
                            )

                            HorizontalDivider(color = BorderLight.copy(alpha = 0.5f), thickness = 1.dp)

                            MenuItemRow(
                                title = "Logout",
                                icon = Icons.AutoMirrored.Filled.ExitToApp,
                                titleColor = ExpenseRed,
                                iconColor = ExpenseRed,
                                testTag = "menu_item_logout",
                                onClick = { showLogoutDialog = true }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // App Lock Configuration Dialog
        if (showAppLockConfigDialog) {
            AlertDialog(
                onDismissRequest = { showAppLockConfigDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RoyalPurplePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = if (adminSecurity.isAppLockEnabled) "App Lock Settings" else "Enable App Lock",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Set a 4-digit numeric PIN for fast unlock. Your master password also works as a fallback.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = appLockPinInput,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    appLockPinInput = it
                                    pinError = null
                                }
                            },
                            label = { Text("4-Digit PIN (Optional if already set)") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            isError = pinError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalPurplePrimary,
                                unfocusedBorderColor = BorderLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_applock_pin")
                        )

                        if (pinError != null) {
                            Text(
                                text = pinError ?: "",
                                fontSize = 11.sp,
                                color = ExpenseRed
                            )
                        }

                        Text(
                            text = "Auto-Lock Timeout on Inactivity:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                0 to "Immediate",
                                1 to "1 Min",
                                5 to "5 Min",
                                15 to "15 Min"
                            ).forEach { (minutes, label) ->
                                FilterChip(
                                    selected = selectedTimeoutMinutes == minutes,
                                    onClick = { selectedTimeoutMinutes = minutes },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalPurplePrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (appLockPinInput.isNotEmpty() && appLockPinInput.length != 4) {
                                pinError = "PIN must be exactly 4 digits"
                                return@Button
                            }
                            showAppLockConfigDialog = false
                            onConfigureAppLock(
                                true,
                                appLockPinInput,
                                selectedTimeoutMinutes,
                                false
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("SAVE & ACTIVATE", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAppLockConfigDialog = false }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }

        // Clear Data Confirmation Dialog
        if (showClearDataDialog) {
            AlertDialog(
                onDismissRequest = { showClearDataDialog = false },
                title = { Text("Reset / Clear All Data", fontWeight = FontWeight.Bold) },
                text = { Text("This will permanently clear all recorded donations and expenses from the database. Are you sure?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDataDialog = false
                            onClearDataClick()
                        }
                    ) {
                        Text("CLEAR ALL", color = ExpenseRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDataDialog = false }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Logout Confirmation", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out? All your financial data, donors, and receipts will remain safely stored in the database.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            onLogoutClick()
                        }
                    ) {
                        Text("LOGOUT", color = ExpenseRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("CANCEL", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun MenuItemRow(
    title: String,
    icon: ImageVector,
    titleColor: Color = TextPrimary,
    iconColor: Color = TextSecondary,
    testTag: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
    }
}


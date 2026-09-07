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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FestivalSettings
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTextField
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FestivalSettingsScreen(
    currentSettings: FestivalSettings,
    onBack: () -> Unit,
    onSaveSettings: (FestivalSettings) -> Unit
) {
    var organizationName by remember(currentSettings) { mutableStateOf(currentSettings.organizationName) }
    var festivalName by remember(currentSettings) { mutableStateOf(currentSettings.festivalName) }
    var festivalYear by remember(currentSettings) { mutableStateOf(currentSettings.festivalYear) }
    var startDate by remember(currentSettings) { mutableStateOf(currentSettings.startDate) }
    var endDate by remember(currentSettings) { mutableStateOf(currentSettings.endDate) }
    var organizationAddress by remember(currentSettings) { mutableStateOf(currentSettings.organizationAddress) }

    var orgNameError by remember { mutableStateOf<String?>(null) }
    var festivalNameError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_festival_settings")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Festival Settings",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Info Banner
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
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Configure festival parameters. Changes automatically update receipts, reports, and header statements across all modules.",
                            fontSize = 12.sp,
                            color = Color(0xFF4A154B),
                            lineHeight = 16.sp
                        )
                    }
                }

                // Live Preview Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "RECEIPT & REPORT HEADER PREVIEW",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurplePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = organizationName.ifBlank { "Organization Name" }.uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${festivalName.ifBlank { "Festival Name" }} $festivalYear",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD97706),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = "Period: ${startDate.ifBlank { "Start Date" }} - ${endDate.ifBlank { "End Date" }}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = organizationAddress.ifBlank { "Organization Address" },
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Form Fields
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
                            text = "Organization & Event Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        AppTextField(
                            value = organizationName,
                            onValueChange = {
                                organizationName = it
                                orgNameError = null
                            },
                            label = "Organization Name *",
                            placeholder = "e.g. Shree Siddhivinayaka Parivar",
                            isError = orgNameError != null,
                            errorMessage = orgNameError,
                            testTag = "input_org_name"
                        )

                        AppTextField(
                            value = festivalName,
                            onValueChange = {
                                festivalName = it
                                festivalNameError = null
                            },
                            label = "Festival Name *",
                            placeholder = "e.g. Ganesh Chaturthi",
                            isError = festivalNameError != null,
                            errorMessage = festivalNameError,
                            testTag = "input_festival_name"
                        )

                        AppTextField(
                            value = festivalYear,
                            onValueChange = { festivalYear = it },
                            label = "Festival Year *",
                            placeholder = "e.g. 2026",
                            testTag = "input_festival_year"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = startDate,
                                    onValueChange = { startDate = it },
                                    label = "Start Date *",
                                    placeholder = "e.g. 14 Sep 2026",
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = RoyalPurplePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    testTag = "input_start_date"
                                )
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                AppTextField(
                                    value = endDate,
                                    onValueChange = { endDate = it },
                                    label = "End Date *",
                                    placeholder = "e.g. 18 Sep 2026",
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = RoyalPurplePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    testTag = "input_end_date"
                                )
                            }
                        }

                        AppTextField(
                            value = organizationAddress,
                            onValueChange = { organizationAddress = it },
                            label = "Organization Address *",
                            placeholder = "e.g. Ganesh Nagar, Anantapur",
                            testTag = "input_org_address"
                        )
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        var hasError = false
                        if (organizationName.isBlank()) {
                            orgNameError = "Organization name is required"
                            hasError = true
                        }
                        if (festivalName.isBlank()) {
                            festivalNameError = "Festival name is required"
                            hasError = true
                        }
                        if (!hasError) {
                            onSaveSettings(
                                FestivalSettings(
                                    id = 1,
                                    organizationName = organizationName.trim(),
                                    festivalName = festivalName.trim(),
                                    festivalYear = festivalYear.trim().ifBlank { "2026" },
                                    startDate = startDate.trim().ifBlank { "14 September 2026" },
                                    endDate = endDate.trim().ifBlank { "18 September 2026" },
                                    organizationAddress = organizationAddress.trim().ifBlank { "Ganesh Nagar, Anantapur" },
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_festival_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SAVE SETTINGS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

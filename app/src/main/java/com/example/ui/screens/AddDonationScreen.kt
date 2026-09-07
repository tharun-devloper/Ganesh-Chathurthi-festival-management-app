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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTextField
import com.example.ui.components.PaymentModeSelector
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddDonationScreen(
    initialDonorName: String = "",
    initialMobileNumber: String = "",
    initialAddress: String = "",
    availableCategories: List<String> = emptyList(),
    onBack: () -> Unit,
    onSaveDonation: (
        name: String,
        mobile: String,
        address: String,
        amount: Double,
        category: String,
        date: String,
        paymentMode: String,
        notes: String
    ) -> Unit
) {
    val todayDateStr = remember {
        java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
    }

    val defaultCategories = listOf(
        "General Donation",
        "Annadanam",
        "Laddu Prasadam",
        "Special Pooja",
        "Idol Sponsorship",
        "Lighting & Pandal",
        "Procession & Music"
    )

    val categories = if (availableCategories.isNotEmpty()) availableCategories else defaultCategories

    var donorName by remember(initialDonorName) { mutableStateOf(initialDonorName) }
    var mobileNumber by remember(initialMobileNumber) { mutableStateOf(initialMobileNumber) }
    var address by remember(initialAddress) { mutableStateOf(initialAddress) }
    var amountText by remember { mutableStateOf("") }
    var category by remember(categories) { mutableStateOf(categories.firstOrNull() ?: "General Donation") }
    var dateText by remember { mutableStateOf(todayDateStr) }
    var paymentMode by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    var showCategoryDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_add_donation")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Add Donation",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Donor Details
                Text(
                    text = "Donor Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                AppTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = "Donor Name *",
                    placeholder = "Enter donor name",
                    testTag = "input_donor_name"
                )

                AppTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = "Mobile Number *",
                    placeholder = "Enter 10-digit mobile number",
                    trailingIcon = {
                        IconButton(onClick = { /* Pick contact */ }) {
                            Icon(
                                imageVector = Icons.Default.ContactPhone,
                                contentDescription = "Pick Contact",
                                tint = TextSecondary
                            )
                        }
                    },
                    testTag = "input_donor_mobile"
                )

                AppTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address",
                    placeholder = "Enter address or locality",
                    testTag = "input_donor_address"
                )

                AppTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = "Donation Amount *",
                    placeholder = "₹ Enter amount",
                    leadingIcon = {
                        Text(
                            text = "₹",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurplePrimary,
                            modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                        )
                    },
                    testTag = "input_donation_amount"
                )

                // Category Dropdown
                Column {
                    Text(
                        text = "Donation Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            .clickable { showCategoryDropdown = true }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = category, fontSize = 14.sp, color = TextPrimary)
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select",
                                tint = TextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Date Picker field
                AppTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = "Date *",
                    placeholder = "Select date",
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar",
                            tint = TextSecondary
                        )
                    },
                    testTag = "input_donation_date"
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Section: Payment Details
                Text(
                    text = "Payment Details",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                PaymentModeSelector(
                    selectedMode = paymentMode,
                    onModeSelected = { paymentMode = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action Save Button
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        val name = donorName.ifBlank { "Devotee" }
                        val mob = mobileNumber.trim()
                        if (amt > 0.0) {
                            onSaveDonation(
                                name,
                                mob,
                                address,
                                amt,
                                category,
                                dateText,
                                paymentMode,
                                notes
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_donation")
                ) {
                    Text(
                        text = "SAVE DONATION",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}


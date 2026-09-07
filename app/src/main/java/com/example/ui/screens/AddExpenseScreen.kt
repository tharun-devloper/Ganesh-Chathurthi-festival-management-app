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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTextField
import com.example.ui.components.PaymentModeSelector
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddExpenseScreen(
    availableCategories: List<String> = emptyList(),
    onBack: () -> Unit,
    onSaveExpense: (
        category: String,
        description: String,
        amount: Double,
        date: String,
        vendor: String,
        paymentMode: String,
        notes: String
    ) -> Unit
) {
    val todayDateStr = remember {
        java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
    }

    val defaultCategories = listOf(
        "Tent & Decoration",
        "Sound System",
        "Lighting",
        "Annadanam",
        "Idol",
        "Printing",
        "Transport",
        "Others"
    )

    val categories = if (availableCategories.isNotEmpty()) availableCategories else defaultCategories

    var category by remember(categories) { mutableStateOf(categories.firstOrNull() ?: "Others") }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(todayDateStr) }
    var vendor by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    var showCategoryDropdown by remember { mutableStateOf(false) }

    // Validation error states
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_add_expense")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Add Expense",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Expense Category *
                Column {
                    Text(
                        text = "Expense Category *",
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
                            .testTag("dropdown_expense_category")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = category, fontSize = 14.sp, color = TextPrimary)
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Category",
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

                // Description *
                Column {
                    AppTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            if (it.isNotBlank()) descriptionError = null
                        },
                        label = "Description *",
                        placeholder = "e.g. Stage Setup & Flower Garland",
                        testTag = "input_expense_desc"
                    )
                    if (descriptionError != null) {
                        Text(
                            text = descriptionError!!,
                            color = ExpenseRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // Amount *
                Column {
                    AppTextField(
                        value = amountText,
                        onValueChange = { input ->
                            // Allow digits and at most one decimal point
                            val filtered = input.filter { it.isDigit() || it == '.' }
                            if (filtered.count { it == '.' } <= 1) {
                                amountText = filtered
                                val amt = filtered.toDoubleOrNull()
                                if (amt != null && amt > 0) {
                                    amountError = null
                                }
                            }
                        },
                        label = "Amount *",
                        placeholder = "Enter amount",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Text(
                                text = "₹",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurplePrimary,
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                            )
                        },
                        testTag = "input_expense_amount"
                    )
                    if (amountError != null) {
                        Text(
                            text = amountError!!,
                            color = ExpenseRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // Date *
                Column {
                    AppTextField(
                        value = dateText,
                        onValueChange = {
                            dateText = it
                            if (it.isNotBlank()) dateError = null
                        },
                        label = "Date *",
                        placeholder = "e.g. 28 Aug 2026",
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar",
                                tint = TextSecondary
                            )
                        },
                        testTag = "input_expense_date"
                    )
                    if (dateError != null) {
                        Text(
                            text = dateError!!,
                            color = ExpenseRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // Paid To / Vendor
                AppTextField(
                    value = vendor,
                    onValueChange = { vendor = it },
                    label = "Paid To / Vendor",
                    placeholder = "e.g. Sri Balaji Decorators",
                    testTag = "input_expense_vendor"
                )

                // Payment Mode
                PaymentModeSelector(
                    selectedMode = paymentMode,
                    onModeSelected = { paymentMode = it }
                )

                // Notes (Optional)
                AppTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes (Optional)",
                    placeholder = "Additional expense remarks",
                    singleLine = false,
                    maxLines = 3,
                    testTag = "input_expense_notes"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SAVE EXPENSE Button
                Button(
                    onClick = {
                        var isValid = true

                        val trimmedDesc = description.trim()
                        if (trimmedDesc.isBlank()) {
                            descriptionError = "Please enter expense description"
                            isValid = false
                        } else {
                            descriptionError = null
                        }

                        val amt = amountText.toDoubleOrNull()
                        if (amt == null || amt <= 0.0) {
                            amountError = "Please enter a valid positive amount (> ₹0)"
                            isValid = false
                        } else {
                            amountError = null
                        }

                        val trimmedDate = dateText.trim()
                        if (trimmedDate.isBlank()) {
                            dateError = "Please enter a valid date"
                            isValid = false
                        } else {
                            dateError = null
                        }

                        if (isValid && amt != null && amt > 0.0) {
                            onSaveExpense(
                                category.trim(),
                                trimmedDesc,
                                amt,
                                trimmedDate,
                                vendor.trim(),
                                paymentMode.trim(),
                                notes.trim()
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_expense")
                ) {
                    Text(
                        text = "SAVE EXPENSE",
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

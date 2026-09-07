package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Foundation
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.ui.CurrencyUtils
import com.example.ui.components.AppHeader
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLightBg
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExpensesListScreen(
    expenses: List<Expense>,
    totalExpenses: Double,
    selectedCategory: String,
    selectedPaymentMode: String = "All",
    searchQuery: String,
    onCategorySelected: (String) -> Unit,
    onPaymentModeSelected: (String) -> Unit = {},
    onSearchQueryChange: (String) -> Unit,
    onAddExpenseClick: () -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    val categoryFilterChips = listOf(
        FilterChipData("All", Icons.Default.ViewModule),
        FilterChipData("Tent & Decoration", Icons.Default.Foundation),
        FilterChipData("Sound System", Icons.Default.Campaign),
        FilterChipData("Lighting", Icons.Default.Lightbulb),
        FilterChipData("Annadanam", Icons.Default.Restaurant),
        FilterChipData("Idol", Icons.Default.Star),
        FilterChipData("Printing", Icons.Default.Print),
        FilterChipData("Transport", Icons.Default.LocalShipping),
        FilterChipData("Others", Icons.Default.MoreHoriz)
    )

    val paymentModes = listOf("All", "Cash", "UPI", "Bank Transfer")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .testTag("screen_expenses_list")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Expenses",
                navigationIcon = Icons.Default.Menu,
                onNavIconClick = {},
                actions = {
                    IconButton(
                        onClick = { isSearchActive = !isSearchActive },
                        modifier = Modifier.testTag("btn_expense_search_toggle")
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                }
            )

            // Search Bar when active
            if (isSearchActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search by description, vendor, or category...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurplePrimary,
                            unfocusedBorderColor = BorderLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_expenses")
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Banner: Total Expenses Card (calculated from Room DB)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL EXPENSES",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = CurrencyUtils.formatInr(totalExpenses),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ExpenseRedLightBg)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${expenses.size} Records",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }

                // Category Filter Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Filter by Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryFilterChips.forEach { chip ->
                                val isSelected = selectedCategory.equals(chip.label, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) RoyalPurplePrimary else Color.White)
                                        .border(
                                            1.dp,
                                            if (isSelected) RoyalPurplePrimary else BorderLight,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onCategorySelected(chip.label) }
                                        .padding(horizontal = 12.dp, vertical = 7.dp)
                                        .testTag("filter_chip_${chip.label.replace(" ", "_")}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = chip.icon,
                                            contentDescription = chip.label,
                                            tint = if (isSelected) Color.White else TextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = chip.label,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Payment Mode Filter Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Filter by Payment Mode",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            paymentModes.forEach { mode ->
                                val isSelected = selectedPaymentMode.equals(mode, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) RoyalPurplePrimary.copy(alpha = 0.12f) else Color.White)
                                        .border(
                                            1.dp,
                                            if (isSelected) RoyalPurplePrimary else BorderLight,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { onPaymentModeSelected(mode) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("mode_chip_$mode"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (mode == "All") "All Modes" else mode,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) RoyalPurplePrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Expense List items
                if (expenses.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No expenses match the current criteria.",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tap + below to record a new festival expense.",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseCardItem(expense = expense)
                    }
                }
            }
        }

        // FAB (+) to Add Expense
        FloatingActionButton(
            onClick = onAddExpenseClick,
            containerColor = RoyalPurplePrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .size(56.dp)
                .testTag("fab_add_expense")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Expense",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ExpenseCardItem(expense: Expense) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .testTag("expense_card_${expense.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Category Badge
                    Text(
                        text = expense.category,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Description
                    if (expense.description.isNotBlank()) {
                        Text(
                            text = expense.description,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Vendor / Paid To
                    if (expense.vendor.isNotBlank()) {
                        Text(
                            text = "Paid to: ${expense.vendor}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }
                }

                // Amount & Payment Mode
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = CurrencyUtils.formatInr(expense.amount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(RoyalPurplePrimary.copy(alpha = 0.08f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = expense.paymentMode,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = RoyalPurplePrimary
                        )
                    }
                }
            }

            // Date & Notes Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${expense.date}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                if (expense.notes.isNotBlank()) {
                    Text(
                        text = "💬 ${expense.notes}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private data class FilterChipData(
    val label: String,
    val icon: ImageVector
)

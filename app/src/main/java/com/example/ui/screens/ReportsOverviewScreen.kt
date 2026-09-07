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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.data.model.FinancialSummary
import com.example.ui.CurrencyUtils
import com.example.ui.components.AppHeader
import com.example.ui.components.DonutChart
import com.example.ui.components.DonutSlice
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CatAnnadanam
import com.example.ui.theme.CatIdol
import com.example.ui.theme.CatLighting
import com.example.ui.theme.CatOthers
import com.example.ui.theme.CatPrinting
import com.example.ui.theme.CatSound
import com.example.ui.theme.CatTent
import com.example.ui.theme.CatTransport
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLightBg
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenBg
import com.example.ui.theme.OnlineBlue
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReportsOverviewScreen(
    summary: FinancialSummary,
    expenses: List<Expense> = emptyList(),
    onViewDetailedReport: () -> Unit
) {
    var selectedPeriod by remember { mutableStateOf("This Festival (14-18 Sep 2026)") }

    // Payment breakdown
    val cashPercentage = if (summary.totalCollection > 0) ((summary.cashCollection / summary.totalCollection) * 100).toInt() else 0
    val upiPercentage = if (summary.totalCollection > 0) ((summary.upiCollection / summary.totalCollection) * 100).toInt() else 0
    val bankPercentage = if (summary.totalCollection > 0) (100 - cashPercentage - upiPercentage).coerceAtLeast(0) else 0

    val bankGoldColor = Color(0xFFD97706)

    val collectionSlices = remember(summary) {
        if (summary.totalCollection <= 0) {
            emptyList()
        } else {
            listOf(
                DonutSlice(summary.cashCollection.toFloat(), IncomeGreen, "Cash"),
                DonutSlice(summary.upiCollection.toFloat(), OnlineBlue, "UPI"),
                DonutSlice(summary.bankCollection.toFloat(), bankGoldColor, "Bank")
            ).filter { it.value > 0f }
        }
    }

    // Standard initial expense categories
    val standardCategories = listOf(
        "Tent & Decoration" to CatTent,
        "Sound System" to CatSound,
        "Lighting" to CatLighting,
        "Annadanam" to CatAnnadanam,
        "Idol" to CatIdol,
        "Printing" to CatPrinting,
        "Transport" to CatTransport,
        "Others" to CatOthers
    )

    val categoryExpenseTotals = remember(expenses) {
        standardCategories.map { (catName, color) ->
            val totalForCat = expenses.filter {
                if (catName == "Printing") {
                    it.category.equals("Printing", ignoreCase = true) || it.category.equals("Flex & Printing", ignoreCase = true)
                } else if (catName == "Others") {
                    it.category.equals("Others", ignoreCase = true) || standardCategories.none { sc ->
                        sc.first != "Others" && sc.first.equals(it.category, ignoreCase = true)
                    }
                } else {
                    it.category.equals(catName, ignoreCase = true)
                }
            }.sumOf { it.amount }
            CategoryItem(catName, totalForCat, color)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .testTag("screen_reports_overview")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Reports",
                navigationIcon = Icons.Default.Menu,
                onNavIconClick = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Period Dropdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .testTag("dropdown_period_selector")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedPeriod,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Period",
                            tint = TextSecondary
                        )
                    }
                }

                // Summary Metric Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Collection
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = IncomeGreenBg),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_total_collection")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Collection", fontSize = 11.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)
                            Text(
                                text = CurrencyUtils.formatInr(summary.totalCollection),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Total Expenses
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = ExpenseRedLightBg),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("card_total_expenses")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total Expenses", fontSize = 11.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Medium)
                            Text(
                                text = CurrencyUtils.formatInr(summary.totalExpenses),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Current Balance Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (summary.currentBalance >= 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_current_balance")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Balance (Surplus)",
                                fontSize = 11.sp,
                                color = if (summary.currentBalance >= 0) Color(0xFF065F46) else Color(0xFF991B1B),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = CurrencyUtils.formatInr(summary.currentBalance),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (summary.currentBalance >= 0) Color(0xFF059669) else ExpenseRed,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // 2. Collection Summary Card (by Payment Mode)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .testTag("card_collection_summary")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = IncomeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Collection Summary",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DonutChart(
                                slices = collectionSlices,
                                size = 140.dp,
                                strokeWidth = 18.dp,
                                centerTitle = "Total",
                                centerValue = CurrencyUtils.formatInr(summary.totalCollection)
                            )

                            Column(
                                modifier = Modifier.weight(1f).padding(start = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Cash
                                PaymentModeRow(
                                    label = "Cash",
                                    amount = summary.cashCollection,
                                    percent = cashPercentage,
                                    dotColor = IncomeGreen
                                )

                                // UPI
                                PaymentModeRow(
                                    label = "UPI",
                                    amount = summary.upiCollection,
                                    percent = upiPercentage,
                                    dotColor = OnlineBlue
                                )

                                // Bank Transfer
                                PaymentModeRow(
                                    label = "Bank Transfer",
                                    amount = summary.bankCollection,
                                    percent = bankPercentage,
                                    dotColor = bankGoldColor
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BorderLight
                        )

                        // Clear table breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            BreakdownRow("Cash", CurrencyUtils.formatInr(summary.cashCollection))
                            BreakdownRow("UPI", CurrencyUtils.formatInr(summary.upiCollection))
                            BreakdownRow("Bank", CurrencyUtils.formatInr(summary.bankCollection))
                            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                            BreakdownRow("Total Collection", CurrencyUtils.formatInr(summary.totalCollection), isBold = true, isGreen = true)
                        }
                    }
                }

                // 3. Expense Summary Card (by Category)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .testTag("card_expense_summary")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Expense Summary",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = CurrencyUtils.formatInr(summary.totalExpenses),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            categoryExpenseTotals.forEach { cat ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(cat.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = cat.name,
                                            fontSize = 12.sp,
                                            color = if (cat.amount > 0) TextPrimary else TextSecondary,
                                            fontWeight = if (cat.amount > 0) FontWeight.Medium else FontWeight.Normal
                                        )
                                    }
                                    Text(
                                        text = CurrencyUtils.formatInr(cat.amount),
                                        fontSize = 12.sp,
                                        fontWeight = if (cat.amount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (cat.amount > 0) TextPrimary else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // View Detailed Report Button
                Button(
                    onClick = onViewDetailedReport,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_view_detailed_report")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "VIEW DETAILED REPORT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun PaymentModeRow(
    label: String,
    amount: Double,
    percent: Int,
    dotColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text(
                "${CurrencyUtils.formatInr(amount)} ($percent%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isGreen: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) TextPrimary else TextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isGreen) IncomeGreen else TextPrimary
        )
    }
}

private data class CategoryItem(
    val name: String,
    val amount: Double,
    val color: Color
)

package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation
import com.example.data.model.DonorGroup
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
import com.example.ui.theme.ExcelGreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLightBg
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenBg
import com.example.ui.theme.OnlineBlue
import com.example.ui.theme.PdfRed
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.utils.FinancialReportGenerator

@Composable
fun DetailedReportScreen(
    summary: FinancialSummary,
    expenses: List<Expense>,
    donations: List<Donation> = emptyList(),
    donors: List<DonorGroup> = emptyList(),
    festivalName: String = "Ganesh Chaturthi Festival 2026",
    festivalDates: String = "14 Sep 2026 - 18 Sep 2026",
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("This Festival (14-18 Sep 2026)") }

    // Categories Breakdown
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

    val categoryBreakdown = remember(expenses) {
        if (expenses.isEmpty()) {
            emptyList<CategoryExpenseItem>()
        } else {
            standardCategories.map { (catName, color) ->
                val amount = expenses.filter {
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
                CategoryExpenseItem(catName, amount, color)
            }.filter { it.amount > 0 }
        }
    }

    val expenseSlices = remember(categoryBreakdown) {
        if (categoryBreakdown.isEmpty()) {
            emptyList()
        } else {
            categoryBreakdown.map {
                DonutSlice(it.amount.toFloat(), it.color, it.name)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_detailed_report")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Detailed Report",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Festival Header & Period Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .testTag("card_festival_report_header")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SHREE SIDDHIVINAYAKA PARIVAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurplePrimary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = festivalName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = festivalDates,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BorderLight
                        )

                        // Period Filter Dropdown
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppBackground)
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedPeriod,
                                    fontSize = 12.sp,
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
                    }
                }

                // Financial Overview 3-Card Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Collection Card
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = IncomeGreenBg),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Total Collection", fontSize = 10.sp, color = Color(0xFF047857), fontWeight = FontWeight.Medium)
                            Text(
                                text = CurrencyUtils.formatInr(summary.totalCollection),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Expense Card
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ExpenseRedLightBg),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Total Expenses", fontSize = 10.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Medium)
                            Text(
                                text = CurrencyUtils.formatInr(summary.totalExpenses),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Balance Card
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (summary.currentBalance >= 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Net Balance", fontSize = 10.sp, color = if (summary.currentBalance >= 0) Color(0xFF065F46) else Color(0xFF991B1B), fontWeight = FontWeight.Medium)
                            Text(
                                text = CurrencyUtils.formatInr(summary.currentBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (summary.currentBalance >= 0) Color(0xFF059669) else ExpenseRed,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Collection Breakdown (Cash, UPI, Bank Transfer)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
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
                                text = "Collection Breakdown",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReportDetailRow("Cash Collection", CurrencyUtils.formatInr(summary.cashCollection))
                            ReportDetailRow("UPI Collection", CurrencyUtils.formatInr(summary.upiCollection))
                            ReportDetailRow("Bank Transfer", CurrencyUtils.formatInr(summary.bankCollection))
                            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
                            ReportDetailRow(
                                label = "Total Collection",
                                value = CurrencyUtils.formatInr(summary.totalCollection),
                                isBold = true,
                                isGreen = true
                            )
                        }
                    }
                }

                // Expenses by Category Card (with Donut Chart & Zero Handling)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .testTag("card_expense_categories_detailed")
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
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = RoyalPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Expenses by Category",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (summary.totalExpenses <= 0 || categoryBreakdown.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No expense data available",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DonutChart(
                                    slices = expenseSlices,
                                    size = 140.dp,
                                    strokeWidth = 18.dp,
                                    centerTitle = "Total",
                                    centerValue = CurrencyUtils.formatInr(summary.totalExpenses)
                                )

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    categoryBreakdown.forEach { item ->
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
                                                        .background(item.color)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = item.name,
                                                    fontSize = 11.sp,
                                                    color = TextSecondary,
                                                    maxLines = 1
                                                )
                                            }
                                            Text(
                                                text = CurrencyUtils.formatInr(item.amount),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Donor Statistics Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .testTag("card_donor_statistics")
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
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = RoyalPurplePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Donor Statistics",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Donors", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "${summary.totalDonorsCount}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Total Donations", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = "${summary.totalDonationsCount}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Average Donation", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = CurrencyUtils.formatInr(summary.averageDonation),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Action Buttons: DOWNLOAD PDF, EXPORT EXCEL, SHARE REPORT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // DOWNLOAD PDF
                    Button(
                        onClick = {
                            val pdfFile = FinancialReportGenerator.generatePdfReport(
                                context = context,
                                summary = summary,
                                expenses = expenses,
                                donations = donations,
                                festivalName = festivalName,
                                dateRange = festivalDates
                            )
                            onShowMessage("Festival Financial Report PDF generated (${pdfFile.name})")
                            FinancialReportGenerator.shareFile(context, pdfFile, "application/pdf", "Open / Share Report PDF")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PdfRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_report_pdf")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DOWNLOAD PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // EXPORT EXCEL
                    Button(
                        onClick = {
                            val csvFile = FinancialReportGenerator.generateExcelReport(
                                context = context,
                                summary = summary,
                                expenses = expenses,
                                donations = donations,
                                donors = donors
                            )
                            onShowMessage("Festival Accounts exported successfully (${csvFile.name})")
                            FinancialReportGenerator.shareFile(context, csvFile, "text/csv", "Open / Share Excel Report")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ExcelGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_report_excel")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("EXPORT EXCEL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // SHARE REPORT
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            val reportContent = buildString {
                                appendLine("📊 SHREE SIDDHIVINAYAKA PARIVAR")
                                appendLine("Ganesh Chaturthi Festival 2026 - Financial Statement")
                                appendLine("Period: $festivalDates")
                                appendLine("------------------------------------------")
                                appendLine("💰 Total Collection: ${CurrencyUtils.formatInr(summary.totalCollection)}")
                                appendLine("   • Cash: ${CurrencyUtils.formatInr(summary.cashCollection)}")
                                appendLine("   • UPI: ${CurrencyUtils.formatInr(summary.upiCollection)}")
                                appendLine("   • Bank: ${CurrencyUtils.formatInr(summary.bankCollection)}")
                                appendLine("📉 Total Expenses: ${CurrencyUtils.formatInr(summary.totalExpenses)}")
                                appendLine("✨ Current Balance: ${CurrencyUtils.formatInr(summary.currentBalance)}")
                                appendLine("👥 Donors: ${summary.totalDonorsCount} | Receipts: ${summary.totalDonationsCount}")
                                appendLine("------------------------------------------")
                                appendLine("Ganpati Bappa Morya! 🙏")
                            }
                            putExtra(Intent.EXTRA_TEXT, reportContent)
                            putExtra(Intent.EXTRA_SUBJECT, "Shree Siddhivinayaka Parivar - Financial Report 2026")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Festival Report"))
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_report_share")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("SHARE REPORT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ReportDetailRow(
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

private data class CategoryExpenseItem(
    val name: String,
    val amount: Double,
    val color: Color
)

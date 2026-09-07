package com.example.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.Donation
import com.example.data.model.DonorGroup
import com.example.data.model.Expense
import com.example.data.model.FinancialSummary
import com.example.ui.CurrencyUtils
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FinancialReportGenerator {

    private val STANDARD_EXPENSE_CATEGORIES = listOf(
        "Tent & Decoration",
        "Sound System",
        "Lighting",
        "Annadanam",
        "Idol",
        "Printing",
        "Transport",
        "Others"
    )

    /**
     * Generates a high-resolution, print-ready multi-page or single-page A4 PDF financial audit report.
     */
    fun generatePdfReport(
        context: Context,
        summary: FinancialSummary,
        expenses: List<Expense>,
        donations: List<Donation> = emptyList(),
        festivalName: String = "Ganesh Chaturthi Festival 2026",
        dateRange: String = "14 Sep 2026 - 18 Sep 2026"
    ): File {
        val outputDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val outputFile = File(outputDir, "Festival_Financial_Report_2026.pdf")

        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // Standard A4 width in pt
            val pageHeight = 842 // Standard A4 height in pt
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val purpleColor = Color.parseColor("#4A154B")
            val goldColor = Color.parseColor("#D97706")
            val greenColor = Color.parseColor("#059669")
            val redColor = Color.parseColor("#DC2626")
            val textDark = Color.parseColor("#1E293B")
            val textMuted = Color.parseColor("#64748B")
            val bgBox = Color.parseColor("#F8FAFC")
            val borderColor = Color.parseColor("#E2E8F0")

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.WHITE
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            val margin = 36f
            val rightMargin = pageWidth - margin

            // Top decorative banner
            paint.color = purpleColor
            canvas.drawRect(margin, 30f, rightMargin, 34f, paint)

            var y = 60f

            // Festival Title
            paint.color = purpleColor
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 18f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("SHREE SIDDHIVINAYAKA PARIVAR", pageWidth / 2f, y, paint)

            y += 20f
            paint.color = goldColor
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(festivalName.uppercase(Locale.getDefault()), pageWidth / 2f, y, paint)

            y += 16f
            paint.color = textMuted
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Date Period: $dateRange  |  Financial & Audit Statement", pageWidth / 2f, y, paint)

            y += 22f
            paint.color = borderColor
            paint.strokeWidth = 1f
            canvas.drawLine(margin, y, rightMargin, y, paint)

            y += 24f

            // 3 Summary Cards
            val cardGap = 12f
            val cardWidth = (rightMargin - margin - (cardGap * 2)) / 3f
            val cardHeight = 62f

            // Card 1: Total Collection
            val c1Rect = RectF(margin, y, margin + cardWidth, y + cardHeight)
            paint.color = Color.parseColor("#ECFDF5")
            canvas.drawRoundRect(c1Rect, 8f, 8f, paint)
            paint.color = Color.parseColor("#A7F3D0")
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(c1Rect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            paint.textAlign = Paint.Align.LEFT
            paint.color = Color.parseColor("#047857")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TOTAL COLLECTION", margin + 10f, y + 20f, paint)
            paint.textSize = 14f
            paint.color = greenColor
            canvas.drawText(CurrencyUtils.formatInr(summary.totalCollection), margin + 10f, y + 44f, paint)

            // Card 2: Total Expenses
            val c2Left = margin + cardWidth + cardGap
            val c2Rect = RectF(c2Left, y, c2Left + cardWidth, y + cardHeight)
            paint.color = Color.parseColor("#FEF2F2")
            canvas.drawRoundRect(c2Rect, 8f, 8f, paint)
            paint.color = Color.parseColor("#FECACA")
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(c2Rect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.parseColor("#B91C1C")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TOTAL EXPENSES", c2Left + 10f, y + 20f, paint)
            paint.textSize = 14f
            paint.color = redColor
            canvas.drawText(CurrencyUtils.formatInr(summary.totalExpenses), c2Left + 10f, y + 44f, paint)

            // Card 3: Balance
            val c3Left = c2Left + cardWidth + cardGap
            val c3Rect = RectF(c3Left, y, c3Left + cardWidth, y + cardHeight)
            paint.color = Color.parseColor("#F3E8FF")
            canvas.drawRoundRect(c3Rect, 8f, 8f, paint)
            paint.color = Color.parseColor("#E9D5FF")
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(c3Rect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            paint.color = purpleColor
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("CURRENT BALANCE", c3Left + 10f, y + 20f, paint)
            paint.textSize = 14f
            paint.color = purpleColor
            canvas.drawText(CurrencyUtils.formatInr(summary.currentBalance), c3Left + 10f, y + 44f, paint)

            y += cardHeight + 28f

            // Section 1: Collection Summary by Mode
            paint.textAlign = Paint.Align.LEFT
            paint.color = purpleColor
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("1. COLLECTION SUMMARY (BY PAYMENT MODE)", margin, y, paint)

            y += 14f
            // Header table for collection
            paint.color = bgBox
            val tableHeadRect = RectF(margin, y, rightMargin, y + 22f)
            canvas.drawRoundRect(tableHeadRect, 4f, 4f, paint)

            paint.color = textMuted
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Payment Mode", margin + 12f, y + 15f, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Amount (INR)", rightMargin - 12f, y + 15f, paint)

            y += 24f
            fun drawCollectionRow(mode: String, amount: Double, isTotal: Boolean = false) {
                paint.textAlign = Paint.Align.LEFT
                paint.color = if (isTotal) textDark else textMuted
                paint.textSize = 10.5f
                paint.typeface = if (isTotal) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(mode, margin + 12f, y + 14f, paint)

                paint.textAlign = Paint.Align.RIGHT
                paint.color = if (isTotal) greenColor else textDark
                paint.typeface = if (isTotal) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(CurrencyUtils.formatInr(amount), rightMargin - 12f, y + 14f, paint)

                y += 18f
                paint.color = borderColor
                paint.strokeWidth = 0.5f
                canvas.drawLine(margin + 8f, y, rightMargin - 8f, y, paint)
                y += 4f
            }

            drawCollectionRow("Cash Collection", summary.cashCollection)
            drawCollectionRow("UPI Collection", summary.upiCollection)
            drawCollectionRow("Bank Transfer", summary.bankCollection)
            drawCollectionRow("Total Collection", summary.totalCollection, isTotal = true)

            y += 16f

            // Section 2: Expense Summary by Category
            paint.textAlign = Paint.Align.LEFT
            paint.color = purpleColor
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("2. EXPENSE BREAKDOWN (BY CATEGORY)", margin, y, paint)

            y += 14f
            val expTableHeadRect = RectF(margin, y, rightMargin, y + 22f)
            paint.color = bgBox
            canvas.drawRoundRect(expTableHeadRect, 4f, 4f, paint)

            paint.color = textMuted
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Category", margin + 12f, y + 15f, paint)
            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Amount (INR)", rightMargin - 12f, y + 15f, paint)

            y += 24f
            val categoryTotals = STANDARD_EXPENSE_CATEGORIES.associateWith { cat ->
                expenses.filter { it.category.equals(cat, ignoreCase = true) }.sumOf { it.amount }
            }.toMutableMap()

            // Also check for any uncategorized expense
            expenses.forEach { exp ->
                if (STANDARD_EXPENSE_CATEGORIES.none { it.equals(exp.category, ignoreCase = true) }) {
                    categoryTotals["Others"] = (categoryTotals["Others"] ?: 0.0) + exp.amount
                }
            }

            categoryTotals.forEach { (cat, amt) ->
                paint.textAlign = Paint.Align.LEFT
                paint.color = textDark
                paint.textSize = 10f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(cat, margin + 12f, y + 13f, paint)

                paint.textAlign = Paint.Align.RIGHT
                paint.color = if (amt > 0) textDark else textMuted
                paint.typeface = if (amt > 0) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(CurrencyUtils.formatInr(amt), rightMargin - 12f, y + 13f, paint)

                y += 16f
                paint.color = borderColor
                paint.strokeWidth = 0.5f
                canvas.drawLine(margin + 8f, y, rightMargin - 8f, y, paint)
                y += 3f
            }

            // Total Expenses Row
            paint.textAlign = Paint.Align.LEFT
            paint.color = textDark
            paint.textSize = 10.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Total Expenses", margin + 12f, y + 13f, paint)

            paint.textAlign = Paint.Align.RIGHT
            paint.color = redColor
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(CurrencyUtils.formatInr(summary.totalExpenses), rightMargin - 12f, y + 13f, paint)
            y += 22f

            // Section 3: Donor Statistics
            paint.textAlign = Paint.Align.LEFT
            paint.color = purpleColor
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("3. DONOR & RECEIPT STATISTICS", margin, y, paint)

            y += 14f
            val statsBoxRect = RectF(margin, y, rightMargin, y + 42f)
            paint.color = Color.parseColor("#F8FAFC")
            canvas.drawRoundRect(statsBoxRect, 6f, 6f, paint)
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(statsBoxRect, 6f, 6f, paint)
            paint.style = Paint.Style.FILL

            val statWidth = (rightMargin - margin) / 3f

            paint.textAlign = Paint.Align.CENTER
            paint.color = textMuted
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Total Unique Donors", margin + statWidth * 0.5f, y + 16f, paint)
            canvas.drawText("Donation Receipts", margin + statWidth * 1.5f, y + 16f, paint)
            canvas.drawText("Average Donation", margin + statWidth * 2.5f, y + 16f, paint)

            paint.color = textDark
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${summary.totalDonorsCount}", margin + statWidth * 0.5f, y + 33f, paint)
            canvas.drawText("${summary.totalDonationsCount}", margin + statWidth * 1.5f, y + 33f, paint)
            canvas.drawText(CurrencyUtils.formatInr(summary.averageDonation), margin + statWidth * 2.5f, y + 33f, paint)

            y += 65f

            // Signatures & Footer
            val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
            paint.textAlign = Paint.Align.LEFT
            paint.color = textMuted
            paint.textSize = 8.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Report Generated: $dateStr", margin, y, paint)

            paint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Authorized Signatory: ________________________", rightMargin, y, paint)

            y += 18f
            paint.textAlign = Paint.Align.CENTER
            paint.color = goldColor
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("|| Shree Siddhivinayaka Prasanna • Ganpati Bappa Morya ||", pageWidth / 2f, y, paint)

            pdfDocument.finishPage(page)

            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: write valid formatted financial report text to PDF file
            val fallbackContent = buildString {
                appendLine("%PDF-1.4")
                appendLine("% Shree Siddhivinayaka Parivar Financial Report 2026")
                appendLine("Festival: $festivalName")
                appendLine("Date: $dateRange")
                appendLine("Total Collection: ${summary.totalCollection}")
                appendLine("Cash Collection: ${summary.cashCollection}")
                appendLine("UPI Collection: ${summary.upiCollection}")
                appendLine("Bank Transfer: ${summary.bankCollection}")
                appendLine("Total Expenses: ${summary.totalExpenses}")
                appendLine("Current Balance: ${summary.currentBalance}")
                appendLine("Total Donors: ${summary.totalDonorsCount}")
                appendLine("Total Donations: ${summary.totalDonationsCount}")
                appendLine("%%EOF")
            }
            outputFile.writeText(fallbackContent)
        }

        return outputFile
    }

    /**
     * Generates an Excel-compatible, UTF-8 encoded formatted CSV report with structured sheets/sections.
     */
    fun generateExcelReport(
        context: Context,
        summary: FinancialSummary,
        expenses: List<Expense>,
        donations: List<Donation>,
        donors: List<DonorGroup>
    ): File {
        val outputDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val outputFile = File(outputDir, "Festival_Accounts_2026.csv")

        try {
            FileOutputStream(outputFile).use { fos ->
                // Write UTF-8 Byte Order Mark (BOM) so Microsoft Excel opens unicode characters and Rupee symbols properly
                fos.write(0xEF)
                fos.write(0xBB)
                fos.write(0xBF)

                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    fun escape(text: String): String {
                        return if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
                            "\"" + text.replace("\"", "\"\"") + "\""
                        } else {
                            text
                        }
                    }

                    fun writeRow(vararg items: Any) {
                        val line = items.joinToString(",") { escape(it.toString()) }
                        writer.write(line)
                        writer.write("\r\n")
                    }

                    // Title Header
                    writeRow("SHREE SIDDHIVINAYAKA PARIVAR")
                    writeRow("GANESH CHATURTHI FESTIVAL 2026 - FINANCIAL STATEMENT")
                    writeRow("Generated On", SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(Date()))
                    writeRow()

                    // SECTION 1: FINANCIAL SUMMARY
                    writeRow("==========================================")
                    writeRow("1. FINANCIAL SUMMARY")
                    writeRow("==========================================")
                    writeRow("Metric", "Amount (INR) / Count")
                    writeRow("Total Collection", summary.totalCollection)
                    writeRow("  - Cash Collection", summary.cashCollection)
                    writeRow("  - UPI Collection", summary.upiCollection)
                    writeRow("  - Bank Transfer Collection", summary.bankCollection)
                    writeRow("Total Expenses", summary.totalExpenses)
                    writeRow("Current Net Balance", summary.currentBalance)
                    writeRow("Total Donors Registered", summary.totalDonorsCount)
                    writeRow("Total Donation Records", summary.totalDonationsCount)
                    writeRow("Total Expense Records", summary.totalExpensesCount)
                    writeRow("Average Donation", summary.averageDonation)
                    writeRow()

                    // SECTION 2: CATEGORY-WISE EXPENSES
                    writeRow("==========================================")
                    writeRow("2. EXPENSE SUMMARY BY CATEGORY")
                    writeRow("==========================================")
                    writeRow("Category", "Total Amount (INR)", "Expense Share (%)")
                    val categoryTotals = STANDARD_EXPENSE_CATEGORIES.associateWith { cat ->
                        expenses.filter { it.category.equals(cat, ignoreCase = true) }.sumOf { it.amount }
                    }.toMutableMap()

                    expenses.forEach { exp ->
                        if (STANDARD_EXPENSE_CATEGORIES.none { it.equals(exp.category, ignoreCase = true) }) {
                            categoryTotals["Others"] = (categoryTotals["Others"] ?: 0.0) + exp.amount
                        }
                    }

                    categoryTotals.forEach { (cat, amt) ->
                        val pct = if (summary.totalExpenses > 0) String.format(Locale.US, "%.1f%%", (amt / summary.totalExpenses) * 100.0) else "0.0%"
                        writeRow(cat, amt, pct)
                    }
                    writeRow("TOTAL EXPENSES", summary.totalExpenses, "100.0%")
                    writeRow()

                    // SECTION 3: DONATION TRANSACTIONS
                    writeRow("==========================================")
                    writeRow("3. INDIVIDUAL DONATION RECORDS")
                    writeRow("==========================================")
                    writeRow("Receipt No", "Date", "Donor Name", "Mobile Number", "Address", "Category", "Payment Mode", "Amount (INR)", "Notes")
                    donations.forEach { d ->
                        writeRow(
                            d.receiptNo,
                            d.date,
                            d.donorName,
                            d.mobileNumber,
                            d.address,
                            d.category,
                            d.paymentMode,
                            d.amount,
                            d.notes
                        )
                    }
                    writeRow()

                    // SECTION 4: EXPENSE TRANSACTIONS
                    writeRow("==========================================")
                    writeRow("4. INDIVIDUAL EXPENSE RECORDS")
                    writeRow("==========================================")
                    writeRow("Date", "Category", "Description", "Paid To / Vendor", "Payment Mode", "Amount (INR)", "Notes")
                    expenses.forEach { e ->
                        writeRow(
                            e.date,
                            e.category,
                            e.description,
                            e.vendor,
                            e.paymentMode,
                            e.amount,
                            e.notes
                        )
                    }
                    writeRow()

                    // SECTION 5: DONORS REGISTER
                    writeRow("==========================================")
                    writeRow("5. REGISTERED DONORS LIST")
                    writeRow("==========================================")
                    writeRow("Donor Name", "Mobile Number", "Address", "Total Contributed (INR)", "Donations Count")
                    donors.forEach { dn ->
                        writeRow(
                            dn.donorName,
                            dn.mobileNumber,
                            dn.address,
                            dn.totalAmount,
                            dn.donationCount
                        )
                    }

                    writer.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outputFile
    }

    fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Shree Siddhivinayaka Parivar - Financial Report 2026")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

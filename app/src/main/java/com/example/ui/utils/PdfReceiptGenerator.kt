package com.example.ui.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.model.Donation
import com.example.ui.CurrencyUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object PdfReceiptGenerator {

    /**
     * Generates a PDF file for a given donation in the app cache directory.
     */
    fun generatePdf(context: Context, donation: Donation): File {
        val outputDir = File(context.cacheDir, "receipts").apply { mkdirs() }
        val outputFile = File(outputDir, "${donation.receiptNo}.pdf")

        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // Standard A4 width in points (72 dpi)
            val pageHeight = 842 // Standard A4 height in points
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val purpleColor = Color.parseColor("#4A154B")
            val darkPurple = Color.parseColor("#2D0C30")
            val goldColor = Color.parseColor("#D97706")
            val greenColor = Color.parseColor("#059669")
            val textDark = Color.parseColor("#1E293B")
            val textMuted = Color.parseColor("#64748B")
            val bgBox = Color.parseColor("#F8FAFC")
            val borderColor = Color.parseColor("#E2E8F0")

            // Draw outer background & card
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.WHITE
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            // Card Container
            val cardMargin = 40f
            val cardTop = 50f
            val cardBottom = 780f
            val cardRight = pageWidth - cardMargin
            val cardRect = RectF(cardMargin, cardTop, cardRight, cardBottom)

            paint.color = Color.WHITE
            canvas.drawRoundRect(cardRect, 16f, 16f, paint)

            // Card Border
            paint.color = purpleColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.5f
            canvas.drawRoundRect(cardRect, 16f, 16f, paint)

            // Inner decorative border
            val innerRect = RectF(cardMargin + 6f, cardTop + 6f, cardRight - 6f, cardBottom - 6f)
            paint.color = goldColor
            paint.strokeWidth = 1f
            canvas.drawRoundRect(innerRect, 12f, 12f, paint)
            paint.style = Paint.Style.FILL

            var y = cardTop + 45f

            // Header
            paint.color = purpleColor
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 20f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("SHREE SIDDHIVINAYAKA PARIVAR", pageWidth / 2f, y, paint)

            y += 24f
            paint.color = goldColor
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("GANESH CHATURTHI FESTIVAL 2026", pageWidth / 2f, y, paint)

            y += 28f
            // Badge "DONATION RECEIPT"
            val badgeWidth = 220f
            val badgeHeight = 32f
            val badgeRect = RectF(pageWidth / 2f - badgeWidth / 2f, y - 22f, pageWidth / 2f + badgeWidth / 2f, y + 10f)
            paint.color = Color.parseColor("#F3E8FF")
            canvas.drawRoundRect(badgeRect, 6f, 6f, paint)

            paint.color = purpleColor
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("DONATION RECEIPT", pageWidth / 2f, y, paint)

            // Divider
            y += 25f
            paint.color = borderColor
            paint.strokeWidth = 1.5f
            canvas.drawLine(cardMargin + 24f, y, cardRight - 24f, y, paint)

            // Details Section
            y += 35f
            val leftX = cardMargin + 30f
            val rightX = cardRight - 30f

            fun drawRow(label: String, value: String, isBold: Boolean = false) {
                paint.textAlign = Paint.Align.LEFT
                paint.color = textMuted
                paint.textSize = 12f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(label, leftX, y, paint)

                paint.textAlign = Paint.Align.RIGHT
                paint.color = textDark
                paint.textSize = 12f
                paint.typeface = if (isBold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(value, rightX, y, paint)

                y += 26f
            }

            drawRow("Receipt No.", donation.receiptNo, isBold = true)
            drawRow("Date", donation.date)
            drawRow("Received From", donation.donorName, isBold = true)
            drawRow("Mobile Number", donation.mobileNumber)
            if (donation.address.isNotBlank() && donation.address != "—") {
                drawRow("Address", donation.address)
            }
            drawRow("Category", donation.category)

            // Amount Box
            y += 10f
            val amountBoxHeight = 85f
            val amountRect = RectF(leftX, y, rightX, y + amountBoxHeight)
            paint.color = bgBox
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(amountRect, 10f, 10f, paint)

            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(amountRect, 10f, 10f, paint)
            paint.style = Paint.Style.FILL

            paint.textAlign = Paint.Align.CENTER
            paint.color = textMuted
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Donation Amount", pageWidth / 2f, y + 24f, paint)

            paint.color = purpleColor
            paint.textSize = 24f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(CurrencyUtils.formatInr(donation.amount), pageWidth / 2f, y + 52f, paint)

            val words = CurrencyUtils.convertNumberToWords(donation.amount)
            paint.color = textMuted
            paint.textSize = 10.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            canvas.drawText("($words)", pageWidth / 2f, y + 72f, paint)

            y += amountBoxHeight + 25f

            drawRow("Payment Mode", donation.paymentMode, isBold = true)

            // Divider
            y += 10f
            paint.color = borderColor
            paint.strokeWidth = 1.5f
            canvas.drawLine(cardMargin + 24f, y, cardRight - 24f, y, paint)

            // Thank you Message
            y += 30f
            paint.textAlign = Paint.Align.CENTER
            paint.color = textMuted
            paint.textSize = 11.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Thank you for your valuable contribution towards the Ganesh Chaturthi Festival.", pageWidth / 2f, y, paint)

            y += 20f
            paint.color = purpleColor
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Ganpati Bappa Morya! 🙏", pageWidth / 2f, y, paint)

            // Authorized Signature Area
            y += 50f
            val signX = cardRight - 130f
            paint.textAlign = Paint.Align.CENTER
            paint.color = Color.parseColor("#1E40AF")
            paint.strokeWidth = 2f
            paint.style = Paint.Style.STROKE
            // Draw simulated stylish signature line
            val sigPath = android.graphics.Path().apply {
                moveTo(signX - 45f, y)
                cubicTo(signX - 25f, y - 20f, signX - 10f, y + 10f, signX + 45f, y - 5f)
            }
            canvas.drawPath(sigPath, paint)
            paint.style = Paint.Style.FILL

            y += 10f
            paint.color = textDark
            paint.strokeWidth = 1f
            canvas.drawLine(signX - 60f, y, signX + 60f, y, paint)

            y += 16f
            paint.color = textMuted
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Authorized Sign", signX, y, paint)

            pdfDocument.finishPage(page)

            val fos = FileOutputStream(outputFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
        } catch (e: Throwable) {
            // Fallback: write text-formatted receipt to the PDF output file
            val fallbackContent = "%PDF-1.4\n% Shree Siddhivinayaka Parivar Donation Receipt\nReceipt: ${donation.receiptNo}\nDonor: ${donation.donorName}\nAmount: ${donation.amount}\nDate: ${donation.date}\nPayment Mode: ${donation.paymentMode}\n%%EOF"
            outputFile.writeText(fallbackContent)
        }

        return outputFile
    }

    /**
     * Share receipt text and PDF via Android's native share sheet.
     */
    fun shareReceipt(context: Context, donation: Donation) {
        try {
            val pdfFile = generatePdf(context, donation)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareText = buildString {
                append("✨ Shree Siddhivinayaka Parivar ✨\n")
                append("Ganesh Chaturthi Festival 2026 - Donation Receipt\n\n")
                append("Receipt No: ${donation.receiptNo}\n")
                append("Donor: ${donation.donorName}\n")
                append("Amount: ${CurrencyUtils.formatInr(donation.amount)} (${CurrencyUtils.convertNumberToWords(donation.amount)})\n")
                append("Date: ${donation.date}\n")
                append("Payment Mode: ${donation.paymentMode}\n\n")
                append("Thank you for your valuable contribution towards the festival.\n")
                append("Ganpati Bappa Morya! 🙏")
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "Donation Receipt - ${donation.receiptNo}")
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Share Donation Receipt")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback to text-only share if file provider fails
            val shareText = "✨ Shree Siddhivinayaka Parivar - Donation Receipt ✨\nReceipt No: ${donation.receiptNo}\nDonor: ${donation.donorName}\nAmount: ${CurrencyUtils.formatInr(donation.amount)}\nDate: ${donation.date}\nPayment Mode: ${donation.paymentMode}\nGanpati Bappa Morya! 🙏"
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Share Receipt"))
        }
    }

    /**
     * Downloads/Saves the PDF receipt to public documents or opens it directly with a PDF viewer.
     */
    fun downloadOrOpenPdf(context: Context, donation: Donation, onComplete: (String) -> Unit) {
        try {
            val pdfFile = generatePdf(context, donation)
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            // Open with native PDF viewer
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(viewIntent)
                onComplete("Receipt PDF ready: ${donation.receiptNo}.pdf")
            } catch (e: Exception) {
                // If no direct PDF viewer is available, trigger share/save chooser
                val saveIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "Receipt ${donation.receiptNo}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(saveIntent, "Save Receipt PDF"))
                onComplete("Receipt saved as ${donation.receiptNo}.pdf")
            }
        } catch (e: Exception) {
            onComplete("Error preparing PDF: ${e.localizedMessage}")
        }
    }

    /**
     * Native Android printing integration.
     */
    fun printReceipt(context: Context, donation: Donation, onMessage: (String) -> Unit) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            onMessage("Printing service is not available on this device.")
            return
        }

        try {
            val pdfFile = generatePdf(context, donation)
            val printAdapter = object : PrintDocumentAdapter() {
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes?,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback?,
                    extras: Bundle?
                ) {
                    if (cancellationSignal?.isCanceled == true) {
                        callback?.onLayoutCancelled()
                        return
                    }

                    val info = PrintDocumentInfo.Builder("${donation.receiptNo}.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()

                    callback?.onLayoutFinished(info, true)
                }

                override fun onWrite(
                    pages: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    var input: FileInputStream? = null
                    var output: FileOutputStream? = null
                    try {
                        input = FileInputStream(pdfFile)
                        output = FileOutputStream(destination?.fileDescriptor)

                        val buf = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buf).also { bytesRead = it } > 0) {
                            if (cancellationSignal?.isCanceled == true) {
                                callback?.onWriteCancelled()
                                return
                            }
                            output.write(buf, 0, bytesRead)
                        }

                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } catch (e: Exception) {
                        callback?.onWriteFailed(e.message)
                    } finally {
                        try { input?.close() } catch (ignored: IOException) {}
                        try { output?.close() } catch (ignored: IOException) {}
                    }
                }
            }

            val jobName = "DonationReceipt_${donation.receiptNo}"
            printManager.print(jobName, printAdapter, PrintAttributes.Builder().build())
            onMessage("Starting print job for ${donation.receiptNo}...")
        } catch (e: Exception) {
            onMessage("Failed to initiate print: ${e.localizedMessage}")
        }
    }
}

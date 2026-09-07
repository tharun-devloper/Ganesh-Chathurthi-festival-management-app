package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation
import com.example.ui.CurrencyUtils
import com.example.ui.components.AppHeader
import com.example.ui.components.GaneshaIconBadge
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BorderLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.RoyalPurpleDark
import com.example.ui.theme.RoyalPurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.utils.PdfReceiptGenerator

@Composable
fun DonationReceiptScreen(
    donation: Donation?,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val item = donation ?: Donation(
        receiptNo = "GCP-00000",
        donorName = "—",
        mobileNumber = "—",
        address = "—",
        amount = 0.0,
        category = "—",
        date = "—",
        paymentMode = "—",
        notes = ""
    )

    val amountInWords = CurrencyUtils.convertNumberToWords(item.amount)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .navigationBarsPadding()
            .testTag("screen_donation_receipt")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppHeader(
                title = "Donation Receipt",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            PdfReceiptGenerator.shareReceipt(context, item)
                        },
                        modifier = Modifier.testTag("btn_share_header")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Printable Receipt Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header Logo and Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GaneshaIconBadge(
                                size = 48.dp,
                                showAura = false
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "SHREE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurplePrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "SIDDHIVINAYAKA",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalPurplePrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "PARIVAR",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurplePrimary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Text(
                            text = "GANESH CHATURTHI FESTIVAL 2026",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        // "DONATION RECEIPT" Badge
                        Box(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(RoyalPurplePrimary.copy(alpha = 0.08f))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "DONATION RECEIPT",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurplePrimary,
                                letterSpacing = 1.sp
                            )
                        }

                        HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        // Key Value Metadata
                        ReceiptRow(label = "Receipt No.", value = item.receiptNo, isBold = true)
                        ReceiptRow(label = "Date", value = item.date)
                        ReceiptRow(label = "Received From", value = item.donorName, isBold = true)
                        ReceiptRow(label = "Mobile", value = item.mobileNumber)
                        if (item.address.isNotBlank() && item.address != "—") {
                            ReceiptRow(label = "Address", value = item.address)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prominent Amount Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Amount",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = CurrencyUtils.formatInr(item.amount),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurplePrimary
                            )
                            Text(
                                text = "($amountInWords)",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        ReceiptRow(label = "Payment Mode", value = item.paymentMode)

                        HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Blessing Message
                        Text(
                            text = "Thank you for your valuable contribution towards the Ganesh Chaturthi Festival.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Ganpati Bappa Morya! 🙏",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurplePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Signature Stamp Area
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Stamp icon simulation
                                Canvas(modifier = Modifier.size(width = 80.dp, height = 24.dp)) {
                                    val path = Path().apply {
                                        moveTo(5f, size.height * 0.7f)
                                        cubicTo(25f, 2f, 40f, size.height, 75f, size.height * 0.4f)
                                    }
                                    drawPath(path, color = Color(0xFF1E40AF), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                                }
                                HorizontalDivider(color = TextSecondary, thickness = 1.dp, modifier = Modifier.width(90.dp))
                                Text(
                                    text = "Authorized Sign",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Action Buttons: SHARE, DOWNLOAD PDF, PRINT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // SHARE
                    Button(
                        onClick = {
                            PdfReceiptGenerator.shareReceipt(context, item)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_receipt_share")
                    ) {
                        Text("SHARE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // DOWNLOAD PDF
                    Button(
                        onClick = {
                            PdfReceiptGenerator.downloadOrOpenPdf(context, item) { msg ->
                                onShowMessage(msg)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurpleDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("btn_receipt_pdf")
                    ) {
                        Text("DOWNLOAD PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // PRINT
                    OutlinedButton(
                        onClick = {
                            PdfReceiptGenerator.printReceipt(context, item) { msg ->
                                onShowMessage(msg)
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_receipt_print")
                    ) {
                        Text("PRINT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = TextPrimary
        )
    }
}

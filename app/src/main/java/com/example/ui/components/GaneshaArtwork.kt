package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GaneshaIconBadge(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    showAura: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD54F).copy(alpha = 0.35f),
                        Color(0xFFFFB300).copy(alpha = 0.15f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.85f)) {
            val w = this.size.width
            val h = this.size.height

            // Glow ring
            if (showAura) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFE082), Color(0xFFFF8F00), Color.Transparent),
                        center = Offset(w / 2, h / 2),
                        radius = w * 0.48f
                    ),
                    radius = w * 0.46f,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Crown (Mukut)
            val crownPath = Path().apply {
                moveTo(w * 0.5f, h * 0.12f)
                lineTo(w * 0.68f, h * 0.32f)
                lineTo(w * 0.32f, h * 0.32f)
                close()
            }
            drawPath(
                path = crownPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFF57C00))
                )
            )

            // Crown crest jewel
            drawCircle(
                color = Color(0xFFD32F2F),
                radius = w * 0.04f,
                center = Offset(w * 0.5f, h * 0.22f)
            )

            // Ganesha Head & Ears
            // Left Ear
            val leftEarPath = Path().apply {
                moveTo(w * 0.32f, h * 0.36f)
                cubicTo(w * 0.12f, h * 0.38f, w * 0.12f, h * 0.58f, w * 0.30f, h * 0.56f)
            }
            drawPath(
                path = leftEarPath,
                brush = Brush.horizontalGradient(listOf(Color(0xFFFFCC80), Color(0xFFFFA726))),
                style = Fill
            )
            drawPath(
                path = leftEarPath,
                color = Color(0xFFE65100),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Right Ear
            val rightEarPath = Path().apply {
                moveTo(w * 0.68f, h * 0.36f)
                cubicTo(w * 0.88f, h * 0.38f, w * 0.88f, h * 0.58f, w * 0.70f, h * 0.56f)
            }
            drawPath(
                path = rightEarPath,
                brush = Brush.horizontalGradient(listOf(Color(0xFFFFA726), Color(0xFFFFCC80))),
                style = Fill
            )
            drawPath(
                path = rightEarPath,
                color = Color(0xFFE65100),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Head Center
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFE0B2), Color(0xFFFFB74D)),
                    center = Offset(w * 0.5f, h * 0.44f),
                    radius = w * 0.22f
                ),
                radius = w * 0.18f,
                center = Offset(w * 0.5f, h * 0.44f)
            )

            // Tilak (Trishul & Red Bindi)
            val tilakPath = Path().apply {
                moveTo(w * 0.5f, h * 0.34f)
                lineTo(w * 0.5f, h * 0.42f)
            }
            drawPath(tilakPath, color = Color(0xFFD32F2F), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(color = Color(0xFFD32F2F), radius = w * 0.025f, center = Offset(w * 0.5f, h * 0.40f))

            // Eyes
            drawCircle(color = Color(0xFF263238), radius = w * 0.02f, center = Offset(w * 0.42f, h * 0.43f))
            drawCircle(color = Color(0xFF263238), radius = w * 0.02f, center = Offset(w * 0.58f, h * 0.43f))

            // Trunk (Vakratunda)
            val trunkPath = Path().apply {
                moveTo(w * 0.5f, h * 0.45f)
                cubicTo(w * 0.48f, h * 0.60f, w * 0.44f, h * 0.72f, w * 0.60f, h * 0.72f)
                cubicTo(w * 0.68f, h * 0.72f, w * 0.68f, h * 0.65f, w * 0.62f, h * 0.64f)
            }
            drawPath(
                path = trunkPath,
                brush = Brush.verticalGradient(listOf(Color(0xFFFFB74D), Color(0xFFF57C00))),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Modak in Hand / Trunk Tip
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))),
                radius = w * 0.045f,
                center = Offset(w * 0.64f, h * 0.63f)
            )

            // Lotus Pedestal base
            val lotusLeft = Path().apply {
                moveTo(w * 0.5f, h * 0.88f)
                cubicTo(w * 0.35f, h * 0.82f, w * 0.28f, h * 0.88f, w * 0.22f, h * 0.82f)
                cubicTo(w * 0.32f, h * 0.94f, w * 0.45f, h * 0.92f, w * 0.5f, h * 0.94f)
            }
            val lotusRight = Path().apply {
                moveTo(w * 0.5f, h * 0.88f)
                cubicTo(w * 0.65f, h * 0.82f, w * 0.72f, h * 0.88f, w * 0.78f, h * 0.82f)
                cubicTo(w * 0.68f, h * 0.94f, w * 0.55f, h * 0.92f, w * 0.5f, h * 0.94f)
            }
            drawPath(lotusLeft, color = Color(0xFFEC407A))
            drawPath(lotusRight, color = Color(0xFFE91E63))
        }
    }
}

@Composable
fun LargeGaneshaIllustration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background holy rays / radiant aura
            for (i in 0 until 16) {
                val angle = (i * 22.5) * (Math.PI / 180.0)
                val endX = (w / 2 + Math.cos(angle) * w * 0.44).toFloat()
                val endY = (h * 0.42f + Math.sin(angle) * h * 0.40).toFloat()
                drawLine(
                    color = Color(0xFFFFB800).copy(alpha = 0.18f),
                    start = Offset(w / 2, h * 0.42f),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Hanging Diyas / Bells on sides
            drawHangingDiya(this, Offset(w * 0.14f, 0f), h * 0.35f)
            drawHangingDiya(this, Offset(w * 0.86f, 0f), h * 0.35f)

            // Divine Aura Circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD54F).copy(alpha = 0.4f),
                        Color(0xFFFF9800).copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(w / 2, h * 0.42f),
                    radius = w * 0.42f
                ),
                center = Offset(w / 2, h * 0.42f),
                radius = w * 0.40f
            )

            // Ornate Arch / Prabhavali
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        Color(0xFFFFD700),
                        Color(0xFFFF8F00),
                        Color(0xFFFFE082),
                        Color(0xFFFF8F00),
                        Color(0xFFFFD700)
                    )
                ),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(w * 0.16f, h * 0.12f),
                size = Size(w * 0.68f, h * 0.60f),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Mukut / Golden Crown
            val crownPath = Path().apply {
                moveTo(w * 0.5f, h * 0.14f)
                lineTo(w * 0.66f, h * 0.32f)
                lineTo(w * 0.58f, h * 0.34f)
                lineTo(w * 0.50f, h * 0.25f)
                lineTo(w * 0.42f, h * 0.34f)
                lineTo(w * 0.34f, h * 0.32f)
                close()
            }
            drawPath(
                crownPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFFF57C00))
                )
            )

            // Crown Ruby
            drawCircle(
                color = Color(0xFFD32F2F),
                radius = w * 0.035f,
                center = Offset(w * 0.5f, h * 0.22f)
            )

            // Ears
            val leftEar = Path().apply {
                moveTo(w * 0.34f, h * 0.36f)
                cubicTo(w * 0.14f, h * 0.36f, w * 0.10f, h * 0.54f, w * 0.32f, h * 0.56f)
            }
            drawPath(
                leftEar,
                brush = Brush.horizontalGradient(listOf(Color(0xFFFFE0B2), Color(0xFFFFA726)))
            )
            drawPath(leftEar, color = Color(0xFFE65100), style = Stroke(width = 2.dp.toPx()))

            val rightEar = Path().apply {
                moveTo(w * 0.66f, h * 0.36f)
                cubicTo(w * 0.86f, h * 0.36f, w * 0.90f, h * 0.54f, w * 0.68f, h * 0.56f)
            }
            drawPath(
                rightEar,
                brush = Brush.horizontalGradient(listOf(Color(0xFFFFA726), Color(0xFFFFE0B2)))
            )
            drawPath(rightEar, color = Color(0xFFE65100), style = Stroke(width = 2.dp.toPx()))

            // Face
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80), Color(0xFFFFA726)),
                    center = Offset(w * 0.5f, h * 0.44f),
                    radius = w * 0.20f
                ),
                radius = w * 0.18f,
                center = Offset(w * 0.5f, h * 0.44f)
            )

            // Trishul Tilak
            val tilak = Path().apply {
                moveTo(w * 0.5f, h * 0.35f)
                lineTo(w * 0.5f, h * 0.43f)
                moveTo(w * 0.46f, h * 0.37f)
                cubicTo(w * 0.47f, h * 0.41f, w * 0.5f, h * 0.41f, w * 0.5f, h * 0.41f)
                moveTo(w * 0.54f, h * 0.37f)
                cubicTo(w * 0.53f, h * 0.41f, w * 0.5f, h * 0.41f, w * 0.5f, h * 0.41f)
            }
            drawPath(tilak, color = Color(0xFFD32F2F), style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(color = Color(0xFFFFD700), radius = w * 0.02f, center = Offset(w * 0.5f, h * 0.41f))

            // Eyes & Eyebrows
            drawArc(
                color = Color(0xFF3E2723),
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(w * 0.40f, h * 0.41f),
                size = Size(w * 0.08f, h * 0.04f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = Color(0xFF3E2723),
                startAngle = 190f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = Offset(w * 0.52f, h * 0.41f),
                size = Size(w * 0.08f, h * 0.04f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Trunk (Curved)
            val trunk = Path().apply {
                moveTo(w * 0.5f, h * 0.45f)
                cubicTo(w * 0.48f, h * 0.60f, w * 0.42f, h * 0.70f, w * 0.58f, h * 0.70f)
                cubicTo(w * 0.67f, h * 0.70f, w * 0.68f, h * 0.62f, w * 0.61f, h * 0.61f)
            }
            drawPath(
                trunk,
                brush = Brush.verticalGradient(listOf(Color(0xFFFFCC80), Color(0xFFFFA726), Color(0xFFFB8C00))),
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
            )

            // Modak
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0xFFFFEE58), Color(0xFFFFA000))),
                radius = w * 0.055f,
                center = Offset(w * 0.62f, h * 0.60f)
            )

            // Lotus Pedestal & Garland
            val lotusBase = Path().apply {
                moveTo(w * 0.18f, h * 0.82f)
                cubicTo(w * 0.32f, h * 0.96f, w * 0.68f, h * 0.96f, w * 0.82f, h * 0.82f)
                cubicTo(w * 0.70f, h * 0.88f, w * 0.30f, h * 0.88f, w * 0.18f, h * 0.82f)
            }
            drawPath(
                lotusBase,
                brush = Brush.horizontalGradient(listOf(Color(0xFFF06292), Color(0xFFE91E63), Color(0xFFC2185B)))
            )

            // Lotus Petals
            for (p in -3..3) {
                val cx = w * 0.5f + (p * w * 0.09f)
                val petal = Path().apply {
                    moveTo(cx, h * 0.76f)
                    cubicTo(cx - w * 0.04f, h * 0.84f, cx + w * 0.04f, h * 0.84f, cx, h * 0.76f)
                }
                drawPath(petal, color = Color(0xFFF48FB1))
            }
        }
    }
}

private fun drawHangingDiya(scope: DrawScope, start: Offset, chainLength: Float) {
    with(scope) {
        // Chain
        drawLine(
            color = Color(0xFFFFD54F).copy(alpha = 0.8f),
            start = start,
            end = Offset(start.x, start.y + chainLength),
            strokeWidth = 1.5.dp.toPx()
        )

        val diyaCenter = Offset(start.x, start.y + chainLength)
        // Diya lamp bowl
        val lampPath = Path().apply {
            moveTo(diyaCenter.x - 14f, diyaCenter.y)
            cubicTo(diyaCenter.x - 14f, diyaCenter.y + 12f, diyaCenter.x + 14f, diyaCenter.y + 12f, diyaCenter.x + 14f, diyaCenter.y)
            close()
        }
        drawPath(lampPath, brush = Brush.verticalGradient(listOf(Color(0xFFFFB300), Color(0xFFE65100))))

        // Flame
        val flamePath = Path().apply {
            moveTo(diyaCenter.x, diyaCenter.y - 12f)
            cubicTo(diyaCenter.x + 6f, diyaCenter.y - 4f, diyaCenter.x + 4f, diyaCenter.y, diyaCenter.x, diyaCenter.y)
            cubicTo(diyaCenter.x - 4f, diyaCenter.y, diyaCenter.x - 6f, diyaCenter.y - 4f, diyaCenter.x, diyaCenter.y - 12f)
        }
        drawPath(flamePath, brush = Brush.radialGradient(listOf(Color(0xFFFFF176), Color(0xFFFF5722))))
    }
}

package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BackdropSource
import com.example.ui.theme.AbyssNavy
import com.example.ui.theme.AmberRay
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MintGlow
import com.example.ui.theme.PrismMagenta
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RichBackdropCanvas(
    source: BackdropSource,
    animate: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backdrop_motion")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val t = if (animate) phase else 0.18f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AbyssNavy)
    ) {
        Crossfade(
            targetState = source,
            animationSpec = tween(350),
            label = "backdrop_crossfade"
        ) { currentSource ->
            when (currentSource) {
                BackdropSource.AURORA -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_backdrop_aurora),
                        contentDescription = "Фон Аврора",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BackdropSource.PRISM -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_backdrop_prism),
                        contentDescription = "Фон Призма",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BackdropSource.FLORA -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_backdrop_flora),
                        contentDescription = "Фон Неон",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                BackdropSource.CALIBRATION -> {
                    CalibrationGridCanvas(phase = t)
                }
            }
        }

        // Dynamic luminous refraction orbs floating over image backdrops so movement through lenses is always vivid
        if (source != BackdropSource.CALIBRATION && animate) {
            AnimatedOrbsOverlay(phase = t)
        }
    }
}

@Composable
private fun AnimatedOrbsOverlay(phase: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val angle = phase * 2f * PI.toFloat()
        val w = size.width
        val h = size.height

        val orb1 = Offset(
            x = w * (0.28f + 0.18f * cos(angle)),
            y = h * (0.28f + 0.14f * sin(angle))
        )
        val orb2 = Offset(
            x = w * (0.72f + 0.18f * sin(angle * 1.2f)),
            y = h * (0.62f + 0.16f * cos(angle * 0.9f))
        )
        val orb3 = Offset(
            x = w * (0.50f + 0.24f * cos(angle * 0.7f + 1.5f)),
            y = h * (0.45f + 0.20f * sin(angle * 1.1f + 0.8f))
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CyanPulse.copy(alpha = 0.55f), Color.Transparent),
                center = orb1,
                radius = w * 0.32f
            ),
            radius = w * 0.32f,
            center = orb1
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(PrismMagenta.copy(alpha = 0.45f), Color.Transparent),
                center = orb2,
                radius = w * 0.36f
            ),
            radius = w * 0.36f,
            center = orb2
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AmberRay.copy(alpha = 0.38f), Color.Transparent),
                center = orb3,
                radius = w * 0.25f
            ),
            radius = w * 0.25f,
            center = orb3
        )
    }
}

@Composable
private fun CalibrationGridCanvas(phase: Float) {
    val textMeasurer = rememberTextMeasurer()
    val angle = phase * 2f * PI.toFloat()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        val w = size.width
        val h = size.height
        val cell = 36.dp.toPx()

        // High-contrast checkerboard tiles in subtle slate + deep navy
        val cols = (w / cell).toInt() + 1
        val rows = (h / cell).toInt() + 1
        for (r in 0..rows) {
            for (c in 0..cols) {
                val isLight = (r + c) % 2 == 0
                drawRect(
                    color = if (isLight) Color(0xFF141E33) else Color(0xFF0A0F1D),
                    topLeft = Offset(c * cell, r * cell),
                    size = Size(cell, cell)
                )
            }
        }

        // Sharp calibration lines
        val majorStep = cell * 2f
        var x = 0f
        while (x <= w) {
            drawLine(
                color = Color.White.copy(alpha = 0.18f),
                start = Offset(x, 0f),
                end = Offset(x, h),
                strokeWidth = 1.dp.toPx()
            )
            x += majorStep
        }
        var y = 0f
        while (y <= h) {
            drawLine(
                color = Color.White.copy(alpha = 0.18f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
            y += majorStep
        }

        // RGB / Spectrum vertical stripes for chromatic aberration testing
        val barWidth = 18.dp.toPx()
        val spectrumColors = listOf(
            Color(0xFFFF2D55),
            Color(0xFFFF9500),
            Color(0xFFFFCC00),
            Color(0xFF34C759),
            Color(0xFF00C7BE),
            Color(0xFF38BDF8),
            Color(0xFFAF52DE)
        )
        val totalBarW = spectrumColors.size * barWidth
        val startX = (w - totalBarW) / 2f
        spectrumColors.forEachIndexed { idx, color ->
            drawRoundRect(
                color = color,
                topLeft = Offset(startX + idx * barWidth, h * 0.14f),
                size = Size(barWidth - 4.dp.toPx(), h * 0.72f),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }

        // Moving luminous shapes behind the lens
        val pillX = w * (0.5f + 0.28f * cos(angle))
        val pillY = h * (0.42f + 0.18f * sin(angle * 1.5f))
        drawCircle(
            brush = Brush.linearGradient(listOf(CyanPulse, ElectricViolet)),
            radius = 52.dp.toPx(),
            center = Offset(pillX, pillY)
        )

        val pill2X = w * (0.5f - 0.26f * sin(angle))
        val pill2Y = h * (0.64f + 0.14f * cos(angle * 1.3f))
        drawCircle(
            brush = Brush.linearGradient(listOf(PrismMagenta, AmberRay)),
            radius = 44.dp.toPx(),
            center = Offset(pill2X, pill2Y)
        )

        // Concentric target rings in center
        val center = Offset(w / 2f, h / 2f)
        listOf(56.dp, 104.dp, 152.dp).forEach { r ->
            drawCircle(
                color = MintGlow.copy(alpha = 0.55f),
                radius = r.toPx(),
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Sharp reference labels to inspect optical magnification & blur
        val labelStyle = TextStyle(
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        listOf(0.22f, 0.38f, 0.54f, 0.70f, 0.84f).forEachIndexed { i, fraction ->
            val measured = textMeasurer.measure("LIQUID GLASS • OPTIC ${i + 1}", style = labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset((w - measured.size.width) / 2f, h * fraction)
            )
        }
    }
}

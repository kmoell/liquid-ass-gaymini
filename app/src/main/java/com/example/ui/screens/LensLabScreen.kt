package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.OpenWith
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.BackdropSource
import com.example.model.GlassConfig
import com.example.ui.components.LiquidButton
import com.example.ui.components.LiquidChip
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidSlider
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.IceWhite
import com.example.ui.theme.PrismMagenta
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import kotlin.math.roundToInt

@Composable
fun LensLabScreen(
    config: GlassConfig,
    backdrop: Backdrop,
    contentPadding: PaddingValues,
    onSelectBackdrop: (BackdropSource) -> Unit,
    onUpdateConfig: ((GlassConfig) -> GlassConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var sphereOffsetX by remember { mutableFloatStateOf(-70f) }
    var sphereOffsetY by remember { mutableFloatStateOf(-90f) }
    var squareOffsetX by remember { mutableFloatStateOf(95f) }
    var squareOffsetY by remember { mutableFloatStateOf(-40f) }
    var pillOffsetX by remember { mutableFloatStateOf(0f) }
    var pillOffsetY by remember { mutableFloatStateOf(120f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Compact Telemetry Header
        LiquidGlassCard(
            backdrop = backdrop,
            config = config,
            cornerRadius = 24.dp,
            contentPadding = 12.dp,
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            imageVector = Icons.Rounded.OpenWith,
                            contentDescription = null,
                            tint = CyanPulse,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Перетаскивайте линзы по фону",
                            style = MaterialTheme.typography.titleSmall,
                            color = IceWhite
                        )
                    }
                    Text(
                        text = "RGB:${if (config.chromaticAberration) "ON" else "OFF"} • 3D:${if (config.depthEffect) "ON" else "OFF"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPulse
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackdropSource.entries.forEach { src ->
                        LiquidChip(
                            label = src.label,
                            selected = config.backdropSource == src,
                            onClick = { onSelectBackdrop(src) },
                            backdrop = backdrop,
                            config = config,
                            leadingIcon = Icons.Rounded.Layers
                        )
                    }
                }
            }
        }

        // Free-form Interactive Drag Arena
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            val halfW = with(density) { (maxWidth / 2f).toPx() }
            val halfH = with(density) { (maxHeight / 2f).toPx() }

            // 1. Draggable Spherical Droplet
            DraggableGlassLens(
                offsetX = sphereOffsetX,
                offsetY = sphereOffsetY,
                onDrag = { dx, dy ->
                    sphereOffsetX = (sphereOffsetX + dx).coerceIn(-halfW + 64f, halfW - 64f)
                    sphereOffsetY = (sphereOffsetY + dy).coerceIn(-halfH + 64f, halfH - 64f)
                },
                shape = { CircleShape },
                backdrop = backdrop,
                config = config,
                label = "КАПЛЯ",
                subLabel = "Circle",
                modifier = Modifier
                    .size(124.dp)
                    .testTag("draggable_sphere_lens")
            )

            // 2. Draggable Prism Rounded Square
            DraggableGlassLens(
                offsetX = squareOffsetX,
                offsetY = squareOffsetY,
                onDrag = { dx, dy ->
                    squareOffsetX = (squareOffsetX + dx).coerceIn(-halfW + 72f, halfW - 72f)
                    squareOffsetY = (squareOffsetY + dy).coerceIn(-halfH + 72f, halfH - 72f)
                },
                shape = { RoundedCornerShape(config.cornerRadiusDp.dp) },
                backdrop = backdrop,
                config = config,
                label = "ПРИЗМА",
                subLabel = "${config.cornerRadiusDp.roundToInt()}dp",
                modifier = Modifier
                    .size(136.dp)
                    .testTag("draggable_square_lens")
            )

            // 3. Draggable Magnifying Pill
            DraggableGlassLens(
                offsetX = pillOffsetX,
                offsetY = pillOffsetY,
                onDrag = { dx, dy ->
                    pillOffsetX = (pillOffsetX + dx).coerceIn(-halfW + 96f, halfW - 96f)
                    pillOffsetY = (pillOffsetY + dy).coerceIn(-halfH + 44f, halfH - 44f)
                },
                shape = { CircleShape },
                backdrop = backdrop,
                config = config,
                label = "ОПТИКА • LENS",
                subLabel = "${config.refractionAmountDp.roundToInt()}dp",
                modifier = Modifier
                    .width(204.dp)
                    .height(74.dp)
                    .testTag("draggable_pill_lens")
            )
        }

        // Quick Bottom Tuning Dock
        LiquidGlassCard(
            backdrop = backdrop,
            config = config,
            cornerRadius = 24.dp,
            contentPadding = 14.dp,
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Преломление: ${config.refractionAmountDp.roundToInt()} dp",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrismMagenta
                    )
                    Text(
                        text = "Размытие: ${config.blurRadiusDp.roundToInt()} dp",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanPulse
                    )
                }

                LiquidSlider(
                    value = config.refractionAmountDp,
                    onValueChange = { v -> onUpdateConfig { it.copy(refractionAmountDp = v) } },
                    valueRange = -56f..60f,
                    backdrop = backdrop,
                    config = config,
                    accentColor = PrismMagenta
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        LiquidSlider(
                            value = config.blurRadiusDp,
                            onValueChange = { v -> onUpdateConfig { it.copy(blurRadiusDp = v) } },
                            valueRange = 0f..36f,
                            backdrop = backdrop,
                            config = config,
                            accentColor = CyanPulse
                        )
                    }
                    LiquidButton(
                        onClick = {
                            sphereOffsetX = -70f
                            sphereOffsetY = -90f
                            squareOffsetX = 95f
                            squareOffsetY = -40f
                            pillOffsetX = 0f
                            pillOffsetY = 120f
                        },
                        backdrop = backdrop,
                        config = config,
                        modifier = Modifier.testTag("center_lenses_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CenterFocusStrong,
                            contentDescription = "Центрировать",
                            tint = IceWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DraggableGlassLens(
    offsetX: Float,
    offsetY: Float,
    onDrag: (Float, Float) -> Unit,
    shape: () -> Shape,
    backdrop: Backdrop,
    config: GlassConfig,
    label: String,
    subLabel: String,
    modifier: Modifier = Modifier
) {
    var dragging by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (dragging) 1.09f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "lens_drag_scale"
    )
    val animatedX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "lens_x"
    )
    val animatedY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "lens_y"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(animatedX.roundToInt(), animatedY.roundToInt()) }
            .drawBackdrop(
                backdrop = backdrop,
                shape = shape,
                effects = {
                    colorControls(
                        brightness = 0f,
                        contrast = config.contrast,
                        saturation = config.saturation
                    )
                    if (config.blurRadiusDp > 0.2f) {
                        blur(config.blurRadiusDp.dp.toPx())
                    }
                    val rHeight = (config.refractionHeightDp * if (dragging) 1.25f else 1f).dp.toPx()
                    val rAmount = (config.refractionAmountDp * if (dragging) 1.25f else 1f).dp.toPx()
                    lens(
                        refractionHeight = rHeight.coerceAtLeast(2f),
                        refractionAmount = rAmount,
                        depthEffect = config.depthEffect,
                        chromaticAberration = config.chromaticAberration
                    )
                },
                highlight = { Highlight.Default.copy(alpha = config.highlightAlpha.coerceAtLeast(0.55f)) },
                shadow = { Shadow(radius = if (dragging) 24.dp else config.shadowRadiusDp.dp) },
                innerShadow = { InnerShadow(radius = config.innerShadowRadiusDp.dp) },
                layerBlock = {
                    scaleX = scale
                    scaleY = scale
                },
                onDrawSurface = {
                    drawRect(config.tintPreset.color.copy(alpha = (config.surfaceAlpha * 0.65f).coerceAtLeast(0.04f)))
                }
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragEnd = { dragging = false },
                    onDragCancel = { dragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.92f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall,
                color = CyanPulse.copy(alpha = 0.95f)
            )
        }
    }
}

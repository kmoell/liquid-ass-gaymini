package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.GlassConfig
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.IceWhite
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.exposureAdjustment
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import kotlin.math.roundToInt

fun Modifier.liquidGlassSurface(
    backdrop: Backdrop,
    config: GlassConfig,
    shape: () -> Shape = { RoundedCornerShape(config.cornerRadiusDp.dp) },
    tintColor: Color? = null,
    tintAlpha: Float = config.surfaceAlpha,
    scale: Float = 1f,
    refractionBoost: Float = 1f
): Modifier = this.drawBackdrop(
    backdrop = backdrop,
    shape = shape,
    effects = {
        colorControls(
            brightness = 0f,
            contrast = config.contrast,
            saturation = config.saturation
        )
        exposureAdjustment(config.exposure)
        if (config.blurRadiusDp > 0.2f) {
            blur(config.blurRadiusDp.dp.toPx())
        }
        val rHeight = (config.refractionHeightDp * refractionBoost).dp.toPx()
        val rAmount = (config.refractionAmountDp * refractionBoost).dp.toPx()
        if (rHeight > 0.5f) {
            lens(
                refractionHeight = rHeight,
                refractionAmount = rAmount,
                depthEffect = config.depthEffect,
                chromaticAberration = config.chromaticAberration
            )
        }
    },
    highlight = {
        if (config.highlightAlpha > 0.02f) {
            Highlight.Default.copy(alpha = config.highlightAlpha.coerceIn(0f, 1f))
        } else null
    },
    shadow = {
        if (config.shadowRadiusDp > 0.5f) {
            Shadow(radius = config.shadowRadiusDp.dp)
        } else null
    },
    innerShadow = {
        if (config.innerShadowRadiusDp > 0.5f) {
            InnerShadow(radius = config.innerShadowRadiusDp.dp)
        } else null
    },
    layerBlock = if (scale != 1f) {
        {
            scaleX = scale
            scaleY = scale
        }
    } else null,
    onDrawSurface = {
        val baseColor = tintColor ?: config.tintPreset.color
        if (tintAlpha > 0.005f) {
            drawRect(baseColor.copy(alpha = tintAlpha))
        }
    }
)

@Composable
fun LiquidGlassCard(
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = config.cornerRadiusDp.dp,
    tintColor: Color? = null,
    tintAlpha: Float = config.surfaceAlpha,
    contentPadding: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .liquidGlassSurface(
                backdrop = backdrop,
                config = config,
                shape = { RoundedCornerShape(cornerRadius) },
                tintColor = tintColor,
                tintAlpha = tintAlpha
            )
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    tintColor: Color? = null,
    tintAlpha: Float = config.surfaceAlpha,
    content: @Composable RowScope.() -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "button_scale"
    )
    val boost by animateFloatAsState(
        targetValue = if (pressed) 1.35f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "button_refraction"
    )

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .liquidGlassSurface(
                backdrop = backdrop,
                config = config,
                shape = { CircleShape },
                tintColor = tintColor,
                tintAlpha = if (pressed) (tintAlpha + 0.12f).coerceAtMost(0.6f) else tintAlpha,
                scale = scale,
                refractionBoost = boost
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        }
                    }
                )
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun LiquidIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    tintColor: Color? = null,
    iconTint: Color = IceWhite
) {
    var pressed by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "icon_btn_scale"
    )

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Button
            }
            .liquidGlassSurface(
                backdrop = backdrop,
                config = config,
                shape = { CircleShape },
                tintColor = tintColor,
                scale = scale,
                refractionBoost = if (pressed) 1.35f else 1f
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun LiquidChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    var pressed by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "chip_scale"
    )
    val chipTint = if (selected) CyanPulse else config.tintPreset.color
    val chipAlpha = if (selected) 0.34f else config.surfaceAlpha

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(CircleShape)
            .background(
                if (selected) CyanPulse.copy(alpha = 0.34f)
                else Color.White.copy(alpha = 0.12f)
            )
            .border(
                width = 1.dp,
                color = if (selected) CyanPulse.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.24f),
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        }
                    }
                )
            }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (selected) IceWhite else IceWhite.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = IceWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF34C759)
) {
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current

    val trackWidth = 66.dp
    val trackHeight = 36.dp
    val thumbWidthIdle = 36.dp
    val thumbHeightIdle = 28.dp
    val paddingHorizontal = 4.dp

    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(if (checked) 1f else 0f) }

    val targetFraction = if (isDragging) dragFraction else if (checked) 1f else 0f
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMediumLow),
        label = "toggle_fraction"
    )
    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.24f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "thumb_scale"
    )

    val trackColor by animateColorAsState(
        targetValue = if (fraction > 0.5f) accentColor.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.16f),
        label = "track_color"
    )

    val maxTravelPx = with(density) {
        (trackWidth - thumbWidthIdle - paddingHorizontal * 2).toPx()
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = trackWidth, minHeight = 48.dp)
            .semantics {
                role = Role.Switch
            }
            .pointerInput(checked) {
                detectTapGestures {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCheckedChange(!checked)
                }
            }
            .pointerInput(checked) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragFraction = if (checked) 1f else 0f
                    },
                    onDragEnd = {
                        val next = dragFraction >= 0.5f
                        isDragging = false
                        if (next != checked) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCheckedChange(next)
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragFraction = (dragFraction + dragAmount / maxTravelPx).coerceIn(0f, 1f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Track
        Box(
            modifier = Modifier
                .width(trackWidth)
                .height(trackHeight)
                .clip(CircleShape)
                .background(trackColor)
                .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
                .padding(horizontal = paddingHorizontal),
            contentAlignment = Alignment.CenterStart
        ) {
            // Morphing Liquid Glass Droplet Thumb (refracts backdrop dynamically while dragged)
            val thumbModifier = if (isDragging) {
                Modifier
                    .offset { IntOffset((fraction * maxTravelPx).roundToInt(), 0) }
                    .width(thumbWidthIdle)
                    .height(thumbHeightIdle)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { CircleShape },
                        effects = {
                            blur(2.dp.toPx())
                            lens(
                                refractionHeight = (config.refractionHeightDp * 1.15f).dp.toPx(),
                                refractionAmount = (config.refractionAmountDp * 1.25f).dp.toPx(),
                                depthEffect = true,
                                chromaticAberration = config.chromaticAberration
                            )
                        },
                        highlight = { Highlight.Default.copy(alpha = 0.85f) },
                        shadow = { Shadow(radius = 6.dp) },
                        layerBlock = {
                            scaleX = thumbScale
                            scaleY = thumbScale
                        },
                        onDrawSurface = {
                            drawRect(Color.White.copy(alpha = 0.16f))
                        }
                    )
            } else {
                Modifier
                    .offset { IntOffset((fraction * maxTravelPx).roundToInt(), 0) }
                    .width(thumbWidthIdle)
                    .height(thumbHeightIdle)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.94f))
            }
            Box(modifier = thumbModifier)
        }
    }
}

@Composable
fun LiquidSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    accentColor: Color = CyanPulse
) {
    val haptics = LocalHapticFeedback.current
    var isActive by remember { mutableStateOf(false) }

    val normalized = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    val thumbScale by animateFloatAsState(
        targetValue = if (isActive) 1.32f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "slider_thumb_scale"
    )
    val trackHeight by animateDpAsState(
        targetValue = if (isActive) 12.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "slider_track_height"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val thumbW = 40.dp
        val thumbH = 26.dp
        val maxWidthPx = with(density) { (maxWidth - thumbW).toPx().coerceAtLeast(1f) }
        val thumbWPx = with(density) { thumbW.toPx() }
        val totalWidthPx = with(density) { maxWidth.toPx().coerceAtLeast(1f) }

        fun updateFromPosition(xPx: Float) {
            val f = ((xPx - thumbWPx / 2f) / maxWidthPx).coerceIn(0f, 1f)
            val mapped = valueRange.start + f * (valueRange.endInclusive - valueRange.start)
            onValueChange(mapped)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(valueRange) {
                    detectTapGestures { offset ->
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        updateFromPosition(offset.x)
                    }
                }
                .pointerInput(valueRange) {
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            isActive = true
                            updateFromPosition(offset.x)
                        },
                        onDragEnd = { isActive = false },
                        onDragCancel = { isActive = false },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            updateFromPosition(change.position.x)
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Lightweight Glass Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
                    .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(normalized)
                        .fillMaxHeight()
                        .background(accentColor.copy(alpha = 0.88f))
                )
            }

            // Morphing Liquid Glass Droplet Thumb (activates lens shader on drag)
            val sliderThumbModifier = if (isActive) {
                Modifier
                    .offset { IntOffset((normalized * maxWidthPx).roundToInt(), 0) }
                    .width(thumbW)
                    .height(thumbH)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { CircleShape },
                        effects = {
                            blur(2.dp.toPx())
                            lens(
                                refractionHeight = (config.refractionHeightDp * 1.1f).dp.toPx(),
                                refractionAmount = (config.refractionAmountDp * 1.25f).dp.toPx(),
                                depthEffect = true,
                                chromaticAberration = config.chromaticAberration
                            )
                        },
                        highlight = { Highlight.Default.copy(alpha = 0.9f) },
                        shadow = { Shadow(radius = 8.dp) },
                        layerBlock = {
                            scaleX = thumbScale
                            scaleY = thumbScale
                        },
                        onDrawSurface = {
                            drawRect(Color.White.copy(alpha = 0.16f))
                        }
                    )
            } else {
                Modifier
                    .offset { IntOffset((normalized * maxWidthPx).roundToInt(), 0) }
                    .width(thumbW)
                    .height(thumbH)
                    .shadow(5.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.94f))
            }
            Box(modifier = sliderThumbModifier)
        }
    }
}

@Composable
fun LiquidVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    label: String,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier,
    fillColor: Color = IceWhite
) {
    var active by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (active) 1.04f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "vslider_scale"
    )

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .liquidGlassSurface(
                backdrop = backdrop,
                config = config,
                shape = { RoundedCornerShape(28.dp) },
                scale = scale,
                refractionBoost = if (active) 1.25f else 1f
            )
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (1f - offset.y / size.height.toFloat()).coerceIn(0f, 1f)
                    onValueChange(fraction)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { active = true },
                    onDragEnd = { active = false },
                    onDragCancel = { active = false },
                    onVerticalDrag = { change, _ ->
                        change.consume()
                        val fraction = (1f - change.position.y / size.height.toFloat()).coerceIn(0f, 1f)
                        onValueChange(fraction)
                    }
                )
            }
    ) {
        val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
        val animatedVal by animateFloatAsState(
            targetValue = value.coerceIn(0f, 1f),
            animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
            label = "vslider_fill"
        )

        // Fill level from bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animatedVal)
                .align(Alignment.BottomCenter)
                .background(fillColor.copy(alpha = 0.42f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(animatedVal * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = IceWhite
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = IceWhite,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = IceWhite.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun LiquidSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val animatedIndex by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "segment_index"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .liquidGlassSurface(
                backdrop = backdrop,
                config = config,
                shape = { CircleShape },
                tintAlpha = (config.surfaceAlpha * 0.85f).coerceAtLeast(0.08f)
            )
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / options.size.coerceAtLeast(1)
        val density = LocalDensity.current
        val segmentWidthPx = with(density) { segmentWidth.toPx() }

        // Sliding liquid glass capsule indicator
        Box(
            modifier = Modifier
                .offset { IntOffset((animatedIndex * segmentWidthPx).roundToInt(), 0) }
                .width(segmentWidth)
                .fillMaxHeight()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = {
                        blur(8.dp.toPx())
                        lens(
                            refractionHeight = 10.dp.toPx(),
                            refractionAmount = 20.dp.toPx(),
                            depthEffect = true,
                            chromaticAberration = config.chromaticAberration
                        )
                    },
                    highlight = { Highlight.Default.copy(alpha = 0.85f) },
                    shadow = { Shadow(radius = 6.dp) },
                    onDrawSurface = {
                        drawRect(Color.White.copy(alpha = 0.24f))
                    }
                )
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("segment_${index}")
                        .pointerInput(index) {
                            detectTapGestures {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelect(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) IceWhite else IceWhite.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

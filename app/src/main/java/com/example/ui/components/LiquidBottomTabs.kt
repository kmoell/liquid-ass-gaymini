package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.ToggleOn
import androidx.compose.material.icons.rounded.Tune
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.GlassConfig
import com.example.model.LabTab
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.IceWhite
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
fun LiquidBottomTabs(
    selectedTab: LabTab,
    onTabSelected: (LabTab) -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier
) {
    val tabs = LabTab.entries
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
    val haptics = LocalHapticFeedback.current

    var isDragging by remember { mutableStateOf(false) }
    var dragIndexFloat by remember { mutableFloatStateOf(selectedIndex.toFloat()) }

    val targetIndex = if (isDragging) dragIndexFloat else selectedIndex.toFloat()
    val animatedIndex by animateFloatAsState(
        targetValue = targetIndex,
        animationSpec = spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMediumLow),
        label = "bottom_tab_indicator"
    )
    val indicatorScale by animateFloatAsState(
        targetValue = if (isDragging) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium),
        label = "bottom_tab_scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .height(68.dp)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { CircleShape },
                    effects = {
                        colorControls(
                            brightness = 0f,
                            contrast = config.contrast,
                            saturation = config.saturation
                        )
                        blur(config.blurRadiusDp.coerceAtLeast(10f).dp.toPx())
                        lens(
                            refractionHeight = config.refractionHeightDp.coerceAtLeast(8f).dp.toPx(),
                            refractionAmount = config.refractionAmountDp.dp.toPx(),
                            depthEffect = config.depthEffect,
                            chromaticAberration = config.chromaticAberration
                        )
                    },
                    highlight = { Highlight.Default.copy(alpha = config.highlightAlpha.coerceAtLeast(0.45f)) },
                    shadow = { Shadow(radius = 20.dp) },
                    innerShadow = { InnerShadow(radius = 6.dp) },
                    onDrawSurface = {
                        drawRect(config.tintPreset.color.copy(alpha = config.surfaceAlpha.coerceIn(0.10f, 0.28f)))
                    }
                )
                .padding(5.dp)
        ) {
            val density = LocalDensity.current
            val tabWidth = maxWidth / tabs.size
            val tabWidthPx = with(density) { tabWidth.toPx().coerceAtLeast(1f) }
            val maxIndex = (tabs.size - 1).toFloat()

            // Draggable & sliding glass lens pill indicator
            Box(
                modifier = Modifier
                    .offset { IntOffset((animatedIndex * tabWidthPx).roundToInt(), 0) }
                    .width(tabWidth)
                    .fillMaxHeight()
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { CircleShape },
                        effects = {
                            if (isDragging) {
                                blur(2.dp.toPx())
                                lens(
                                    refractionHeight = 16.dp.toPx(),
                                    refractionAmount = 32.dp.toPx(),
                                    depthEffect = true,
                                    chromaticAberration = true
                                )
                            } else {
                                blur(6.dp.toPx())
                                lens(
                                    refractionHeight = 10.dp.toPx(),
                                    refractionAmount = 20.dp.toPx(),
                                    depthEffect = true,
                                    chromaticAberration = config.chromaticAberration
                                )
                            }
                        },
                        highlight = { Highlight.Default.copy(alpha = 0.85f) },
                        shadow = { Shadow(radius = 8.dp) },
                        layerBlock = {
                            scaleX = indicatorScale
                            scaleY = indicatorScale
                        },
                        onDrawSurface = {
                            drawRect(Color.White.copy(alpha = if (isDragging) 0.16f else 0.22f))
                        }
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(tabs) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                isDragging = true
                                dragIndexFloat = (offset.x / tabWidthPx - 0.5f).coerceIn(0f, maxIndex)
                            },
                            onDragEnd = {
                                val snapped = dragIndexFloat.roundToInt().coerceIn(0, tabs.lastIndex)
                                isDragging = false
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(tabs[snapped])
                            },
                            onDragCancel = {
                                isDragging = false
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                dragIndexFloat = (dragIndexFloat + dragAmount / tabWidthPx).coerceIn(0f, maxIndex)
                            }
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = tab == selectedTab
                    val icon = when (tab) {
                        LabTab.CONTROLS -> if (isSelected) Icons.Rounded.ToggleOn else Icons.Outlined.ToggleOn
                        LabTab.LENS -> if (isSelected) Icons.Rounded.AutoAwesome else Icons.Outlined.AutoAwesome
                        LabTab.SURFACES -> if (isSelected) Icons.Rounded.Layers else Icons.Outlined.Layers
                        LabTab.STUDIO -> if (isSelected) Icons.Rounded.Tune else Icons.Outlined.Tune
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("tab_${tab.name.lowercase()}")
                            .pointerInput(tab) {
                                detectTapGestures {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onTabSelected(tab)
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) CyanPulse else IceWhite.copy(alpha = 0.78f),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) IceWhite else IceWhite.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

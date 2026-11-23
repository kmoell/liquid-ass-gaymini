package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BrightnessHigh
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.GlassConfig
import com.example.ui.components.LiquidButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidIconButton
import com.example.ui.components.LiquidSlider
import com.example.ui.components.LiquidVerticalSlider
import com.example.ui.components.liquidGlassSurface
import com.example.ui.theme.AmberRay
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.IceWhite
import com.example.ui.theme.PrismMagenta
import com.example.viewmodel.ControlsDemoState
import com.kyant.backdrop.Backdrop
import kotlin.math.roundToInt

@Composable
fun SurfacesScreen(
    config: GlassConfig,
    controls: ControlsDemoState,
    backdrop: Backdrop,
    contentPadding: PaddingValues,
    onUpdateControls: ((ControlsDemoState) -> ControlsDemoState) -> Unit,
    onOpenDialog: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Реальные сцены",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IceWhite
                )
                Text(
                    text = "Медиаплеер, Пункт управления и многослойные диалоги",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IceWhite.copy(alpha = 0.78f)
                )
            }
        }

        // Glass Media Player Card
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_album_cover),
                            contentDescription = "Обложка альбома",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Crystal Refraction",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IceWhite
                            )
                            Text(
                                text = "GlassLab Acoustics • 24-bit",
                                style = MaterialTheme.typography.bodyMedium,
                                color = IceWhite.copy(alpha = 0.72f)
                            )
                        }
                        LiquidIconButton(
                            onClick = { onUpdateControls { it.copy(isLiked = !it.isLiked) } },
                            icon = if (controls.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Нравится",
                            backdrop = backdrop,
                            config = config,
                            tintColor = if (controls.isLiked) PrismMagenta else null,
                            iconTint = if (controls.isLiked) PrismMagenta else IceWhite
                        )
                    }

                    LiquidSlider(
                        value = controls.trackProgress,
                        onValueChange = { v -> onUpdateControls { it.copy(trackProgress = v) } },
                        backdrop = backdrop,
                        config = config,
                        accentColor = CyanPulse
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val totalSec = (controls.trackProgress * 224).roundToInt()
                        Text(
                            text = "${totalSec / 60}:${(totalSec % 60).toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.labelSmall,
                            color = IceWhite.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "3:44",
                            style = MaterialTheme.typography.labelSmall,
                            color = IceWhite.copy(alpha = 0.7f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LiquidIconButton(
                            onClick = { onUpdateControls { it.copy(trackProgress = 0f) } },
                            icon = Icons.Rounded.SkipPrevious,
                            contentDescription = "Назад",
                            backdrop = backdrop,
                            config = config
                        )
                        LiquidIconButton(
                            onClick = { onUpdateControls { it.copy(isPlaying = !it.isPlaying) } },
                            icon = if (controls.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = "Старт или пауза",
                            backdrop = backdrop,
                            config = config,
                            size = 64.dp,
                            tintColor = CyanPulse,
                            modifier = Modifier.testTag("media_play_pause")
                        )
                        LiquidIconButton(
                            onClick = { onUpdateControls { it.copy(trackProgress = 0.75f) } },
                            icon = Icons.Rounded.SkipNext,
                            contentDescription = "Вперёд",
                            backdrop = backdrop,
                            config = config
                        )
                    }
                }
            }
        }

        // Control Center Vertical Sliders + Glass Tiles
        item {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .height(210.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LiquidVerticalSlider(
                    value = controls.ccBrightness,
                    onValueChange = { v -> onUpdateControls { it.copy(ccBrightness = v) } },
                    icon = Icons.Rounded.BrightnessHigh,
                    label = "Свет",
                    backdrop = backdrop,
                    config = config,
                    fillColor = AmberRay,
                    modifier = Modifier
                        .weight(0.27f)
                        .fillMaxSize()
                        .testTag("cc_brightness")
                )

                LiquidVerticalSlider(
                    value = controls.ccVolume,
                    onValueChange = { v -> onUpdateControls { it.copy(ccVolume = v) } },
                    icon = Icons.Rounded.VolumeUp,
                    label = "Звук",
                    backdrop = backdrop,
                    config = config,
                    fillColor = CyanPulse,
                    modifier = Modifier
                        .weight(0.27f)
                        .fillMaxSize()
                        .testTag("cc_volume")
                )

                Column(
                    modifier = Modifier
                        .weight(0.46f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ControlCenterTile(
                            icon = Icons.Rounded.Wifi,
                            label = "Wi-Fi",
                            active = controls.wifiEnabled,
                            activeColor = CyanPulse,
                            onClick = { onUpdateControls { it.copy(wifiEnabled = !it.wifiEnabled) } },
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.weight(1f)
                        )
                        ControlCenterTile(
                            icon = Icons.Rounded.Bluetooth,
                            label = "BT",
                            active = controls.bluetoothEnabled,
                            activeColor = ElectricViolet,
                            onClick = { onUpdateControls { it.copy(bluetoothEnabled = !it.bluetoothEnabled) } },
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ControlCenterTile(
                            icon = Icons.Rounded.FlashlightOn,
                            label = "Фонарь",
                            active = controls.ccFlashlight,
                            activeColor = AmberRay,
                            onClick = { onUpdateControls { it.copy(ccFlashlight = !it.ccFlashlight) } },
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.weight(1f)
                        )
                        ControlCenterTile(
                            icon = Icons.Rounded.Bedtime,
                            label = "Фокус",
                            active = controls.ccNightShift,
                            activeColor = PrismMagenta,
                            onClick = { onUpdateControls { it.copy(ccNightShift = !it.ccNightShift) } },
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Overlay / Dialog / Toast Triggers
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Поверхности и диалоги",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IceWhite
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LiquidButton(
                            onClick = onOpenDialog,
                            backdrop = backdrop,
                            config = config,
                            tintColor = ElectricViolet,
                            tintAlpha = 0.28f,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_glass_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.OpenInNew,
                                contentDescription = null,
                                tint = IceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Диалог",
                                style = MaterialTheme.typography.labelLarge,
                                color = IceWhite
                            )
                        }

                        LiquidButton(
                            onClick = { onShowToast("Жидкое стекло активно • ${config.refractionAmountDp.roundToInt()}dp") },
                            backdrop = backdrop,
                            config = config,
                            tintColor = CyanPulse,
                            tintAlpha = 0.24f,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trigger_glass_toast_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsActive,
                                contentDescription = null,
                                tint = IceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Тост",
                                style = MaterialTheme.typography.labelLarge,
                                color = IceWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlCenterTile(
    icon: ImageVector,
    label: String,
    active: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "tile_scale"
    )
    val haptics = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .liquidGlassSurface(
                backdrop = backdrop,
                config = config,
                shape = { RoundedCornerShape(24.dp) },
                tintColor = if (active) activeColor else config.tintPreset.color,
                tintAlpha = if (active) 0.36f else config.surfaceAlpha,
                scale = scale,
                refractionBoost = if (pressed) 1.3f else 1f
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = IceWhite,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = IceWhite
        )
    }
}

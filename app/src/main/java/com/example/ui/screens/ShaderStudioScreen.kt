package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.GlassConfig
import com.example.model.GlassTintPreset
import com.example.ui.components.LiquidButton
import com.example.ui.components.LiquidChip
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidSlider
import com.example.ui.components.LiquidToggle
import com.example.ui.theme.AmberRay
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.IceWhite
import com.example.ui.theme.MintGlow
import com.example.ui.theme.PrismMagenta
import com.kyant.backdrop.Backdrop
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ShaderStudioScreen(
    config: GlassConfig,
    backdrop: Backdrop,
    contentPadding: PaddingValues,
    onUpdateConfig: ((GlassConfig) -> GlassConfig) -> Unit,
    onSelectTint: (GlassTintPreset) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Specimen Preview
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Студия шейдеров",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IceWhite,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                LiquidGlassCard(
                    backdrop = backdrop,
                    config = config,
                    modifier = Modifier.fillMaxWidth()
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
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyanPulse,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Образец Liquid Glass",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IceWhite
                                )
                            }
                            Text(
                                text = config.activePreset?.label ?: "Свой пресет",
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberRay
                            )
                        }

                        Text(
                            text = "Blur ${config.blurRadiusDp.roundToInt()}dp • Height ${config.refractionHeightDp.roundToInt()}dp • Amount ${config.refractionAmountDp.roundToInt()}dp • Sat ${formatFloat(config.saturation)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = IceWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Tint Picker
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = null,
                            tint = PrismMagenta,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Оттенок поверхности",
                            style = MaterialTheme.typography.titleSmall,
                            color = IceWhite
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassTintPreset.entries.forEach { tint ->
                            LiquidChip(
                                label = tint.label,
                                selected = config.tintPreset == tint,
                                onClick = { onSelectTint(tint) },
                                backdrop = backdrop,
                                config = config
                            )
                        }
                    }
                }
            }
        }

        // Optical Refraction & Blur Sliders
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
                        text = "Оптика и преломление",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IceWhite
                    )

                    StudioSlider(
                        label = "Размытие (Blur)",
                        valueText = "${config.blurRadiusDp.roundToInt()} dp",
                        value = config.blurRadiusDp,
                        range = 0f..40f,
                        accent = CyanPulse,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(blurRadiusDp = v) } }

                    StudioSlider(
                        label = "Высота линзы (Refraction Height)",
                        valueText = "${config.refractionHeightDp.roundToInt()} dp",
                        value = config.refractionHeightDp,
                        range = 0f..36f,
                        accent = ElectricViolet,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(refractionHeightDp = v) } }

                    StudioSlider(
                        label = "Искажение (Refraction Amount)",
                        valueText = "${config.refractionAmountDp.roundToInt()} dp",
                        value = config.refractionAmountDp,
                        range = -60f..60f,
                        accent = PrismMagenta,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(refractionAmountDp = v) } }

                    StudioToggle(
                        label = "3D Эффект глубины",
                        checked = config.depthEffect,
                        accent = MintGlow,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(depthEffect = v) } }

                    StudioToggle(
                        label = "Хроматическая аберрация (RGB)",
                        checked = config.chromaticAberration,
                        accent = PrismMagenta,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(chromaticAberration = v) } }
                }
            }
        }

        // Color, Highlights & Geometry Sliders
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
                        text = "Цвет, блики и форма",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IceWhite
                    )

                    StudioSlider(
                        label = "Насыщенность (Saturation)",
                        valueText = "${formatFloat(config.saturation)}x",
                        value = config.saturation,
                        range = 0.4f..2.4f,
                        accent = MintGlow,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(saturation = v) } }

                    StudioSlider(
                        label = "Экспозиция (Exposure)",
                        valueText = "${formatFloat(config.exposure)} EV",
                        value = config.exposure,
                        range = -0.5f..0.5f,
                        accent = AmberRay,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(exposure = v) } }

                    StudioSlider(
                        label = "Плотность тона (Surface Alpha)",
                        valueText = "${(config.surfaceAlpha * 100).roundToInt()}%",
                        value = config.surfaceAlpha,
                        range = 0f..0.45f,
                        accent = CyanPulse,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(surfaceAlpha = v) } }

                    StudioSlider(
                        label = "Свечение граней (Highlight)",
                        valueText = "${(config.highlightAlpha * 100).roundToInt()}%",
                        value = config.highlightAlpha,
                        range = 0f..1f,
                        accent = IceWhite,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(highlightAlpha = v) } }

                    StudioSlider(
                        label = "Скругление углов (Corner Radius)",
                        valueText = "${config.cornerRadiusDp.roundToInt()} dp",
                        value = config.cornerRadiusDp,
                        range = 8f..40f,
                        accent = ElectricViolet,
                        backdrop = backdrop,
                        config = config
                    ) { v -> onUpdateConfig { it.copy(cornerRadiusDp = v) } }

                    Spacer(modifier = Modifier.height(4.dp))

                    LiquidButton(
                        onClick = onReset,
                        backdrop = backdrop,
                        config = config,
                        tintColor = PrismMagenta,
                        tintAlpha = 0.24f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("studio_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            tint = IceWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Сбросить параметры к пресету Кристалл",
                            style = MaterialTheme.typography.labelLarge,
                            color = IceWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioSlider(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    accent: Color,
    backdrop: Backdrop,
    config: GlassConfig,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = IceWhite.copy(alpha = 0.9f)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelMedium,
                color = accent
            )
        }
        LiquidSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            backdrop = backdrop,
            config = config,
            accentColor = accent
        )
    }
}

@Composable
private fun StudioToggle(
    label: String,
    checked: Boolean,
    accent: Color,
    backdrop: Backdrop,
    config: GlassConfig,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = IceWhite
        )
        LiquidToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            backdrop = backdrop,
            config = config,
            accentColor = accent
        )
    }
}

private fun formatFloat(v: Float): String = String.format(Locale.US, "%.2f", v)

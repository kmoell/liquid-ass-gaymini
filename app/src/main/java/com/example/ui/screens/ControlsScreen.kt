package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Flare
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.Wifi
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
import com.example.model.BackdropSource
import com.example.model.GlassConfig
import com.example.model.GlassPreset
import com.example.ui.components.LiquidButton
import com.example.ui.components.LiquidChip
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.LiquidIconButton
import com.example.ui.components.LiquidSegmentedControl
import com.example.ui.components.LiquidSlider
import com.example.ui.components.LiquidToggle
import com.example.ui.theme.AmberRay
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.IceWhite
import com.example.ui.theme.MintGlow
import com.example.ui.theme.PrismMagenta
import com.example.viewmodel.ControlsDemoState
import com.kyant.backdrop.Backdrop
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ControlsScreen(
    config: GlassConfig,
    controls: ControlsDemoState,
    backdrop: Backdrop,
    contentPadding: PaddingValues,
    onSelectPreset: (GlassPreset) -> Unit,
    onSelectBackdrop: (BackdropSource) -> Unit,
    onUpdateConfig: ((GlassConfig) -> GlassConfig) -> Unit,
    onUpdateControls: ((ControlsDemoState) -> ControlsDemoState) -> Unit,
    onToggleChip: (String) -> Unit,
    onButtonTap: () -> Unit,
    onReset: () -> Unit,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header + Quick Presets & Backdrop Switcher
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Полигон стекла",
                    style = MaterialTheme.typography.headlineMedium,
                    color = IceWhite,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Пресеты оптики и выбор подложки в реальном времени",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IceWhite.copy(alpha = 0.78f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassPreset.entries.forEach { preset ->
                        LiquidChip(
                            label = preset.label,
                            selected = config.activePreset == preset,
                            onClick = { onSelectPreset(preset) },
                            backdrop = backdrop,
                            config = config,
                            leadingIcon = Icons.Rounded.AutoAwesome,
                            modifier = Modifier.testTag("preset_${preset.name.lowercase()}")
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BackdropSource.entries.forEach { src ->
                        LiquidChip(
                            label = "Фон: ${src.label}",
                            selected = config.backdropSource == src,
                            onClick = { onSelectBackdrop(src) },
                            backdrop = backdrop,
                            config = config,
                            leadingIcon = Icons.Rounded.Layers,
                            modifier = Modifier.testTag("backdrop_${src.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Liquid Toggles Section
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SectionTitle(
                        title = "Переключатели (LiquidToggle)",
                        subtitle = "Потяните каплю или нажмите — ползунок преломляет фон при движении"
                    )

                    ToggleRow(
                        icon = Icons.Rounded.Wifi,
                        title = "Wi-Fi Стекло",
                        subtitle = if (controls.wifiEnabled) "Подключено • 5 ГГц" else "Отключено",
                        checked = controls.wifiEnabled,
                        onCheckedChange = { v -> onUpdateControls { it.copy(wifiEnabled = v) } },
                        backdrop = backdrop,
                        config = config,
                        accentColor = CyanPulse,
                        tag = "toggle_wifi"
                    )

                    ToggleRow(
                        icon = Icons.Rounded.Bluetooth,
                        title = "Bluetooth Link",
                        subtitle = if (controls.bluetoothEnabled) "Активен • 2 устройства" else "Выключен",
                        checked = controls.bluetoothEnabled,
                        onCheckedChange = { v -> onUpdateControls { it.copy(bluetoothEnabled = v) } },
                        backdrop = backdrop,
                        config = config,
                        accentColor = ElectricViolet,
                        tag = "toggle_bluetooth"
                    )

                    ToggleRow(
                        icon = Icons.Rounded.Flare,
                        title = "Дисперсия RGB",
                        subtitle = "Хроматическая аберрация по граням всех стёкол",
                        checked = config.chromaticAberration,
                        onCheckedChange = { v -> onUpdateConfig { it.copy(chromaticAberration = v) } },
                        backdrop = backdrop,
                        config = config,
                        accentColor = PrismMagenta,
                        tag = "toggle_chromatic"
                    )

                    ToggleRow(
                        icon = Icons.Rounded.Visibility,
                        title = "3D Глубина линзы",
                        subtitle = "Объёмный радиальный изгиб нормалей",
                        checked = config.depthEffect,
                        onCheckedChange = { v -> onUpdateConfig { it.copy(depthEffect = v) } },
                        backdrop = backdrop,
                        config = config,
                        accentColor = MintGlow,
                        tag = "toggle_depth"
                    )

                    ToggleRow(
                        icon = Icons.Rounded.Bolt,
                        title = "Анимация фона",
                        subtitle = "Плавное движение световых сфер за стеклом",
                        checked = config.animateBackdrop,
                        onCheckedChange = { v -> onUpdateConfig { it.copy(animateBackdrop = v) } },
                        backdrop = backdrop,
                        config = config,
                        accentColor = AmberRay,
                        tag = "toggle_animate"
                    )
                }
            }
        }

        // Liquid Sliders Section
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(
                        title = "Слайдеры (LiquidSlider)",
                        subtitle = "Живой контроль оптики и тестовых уровней"
                    )

                    SliderRow(
                        label = "Размытие стекла (Blur)",
                        valueLabel = "${config.blurRadiusDp.roundToInt()} dp",
                        value = config.blurRadiusDp,
                        onValueChange = { v -> onUpdateConfig { it.copy(blurRadiusDp = v) } },
                        valueRange = 0f..36f,
                        backdrop = backdrop,
                        config = config,
                        accentColor = CyanPulse,
                        tag = "slider_blur"
                    )

                    SliderRow(
                        label = "Сила преломления (Refraction)",
                        valueLabel = "${config.refractionAmountDp.roundToInt()} dp",
                        value = config.refractionAmountDp,
                        onValueChange = { v -> onUpdateConfig { it.copy(refractionAmountDp = v) } },
                        valueRange = -52f..56f,
                        backdrop = backdrop,
                        config = config,
                        accentColor = PrismMagenta,
                        tag = "slider_refraction"
                    )

                    SliderRow(
                        label = "Громкость сцены",
                        valueLabel = "${(controls.volumeLevel * 100).roundToInt()}%",
                        value = controls.volumeLevel,
                        onValueChange = { v -> onUpdateControls { it.copy(volumeLevel = v) } },
                        valueRange = 0f..1f,
                        backdrop = backdrop,
                        config = config,
                        accentColor = MintGlow,
                        tag = "slider_volume"
                    )

                    SliderRow(
                        label = "Яркость матрицы",
                        valueLabel = "${(controls.brightnessLevel * 100).roundToInt()}%",
                        value = controls.brightnessLevel,
                        onValueChange = { v -> onUpdateControls { it.copy(brightnessLevel = v) } },
                        valueRange = 0f..1f,
                        backdrop = backdrop,
                        config = config,
                        accentColor = AmberRay,
                        tag = "slider_brightness"
                    )
                }
            }
        }

        // Segmented Control + Buttons + Icon Buttons
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SectionTitle(
                        title = "Кнопки и сегменты",
                        subtitle = "Пружинная отдача и спекулярный блик при нажатии"
                    )

                    LiquidSegmentedControl(
                        options = listOf("Мягкое", "Баланс", "Острое"),
                        selectedIndex = controls.selectedSegment,
                        onSelect = { idx ->
                            onUpdateControls { it.copy(selectedSegment = idx) }
                            when (idx) {
                                0 -> onUpdateConfig { it.copy(blurRadiusDp = 24f, refractionAmountDp = 16f) }
                                1 -> onUpdateConfig { it.copy(blurRadiusDp = 14f, refractionAmountDp = 28f) }
                                2 -> onUpdateConfig { it.copy(blurRadiusDp = 4f, refractionAmountDp = 44f) }
                            }
                        },
                        backdrop = backdrop,
                        config = config
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LiquidButton(
                            onClick = {
                                onButtonTap()
                                onShowToast("Импульс стекла #${controls.buttonTapCount + 1}")
                            },
                            backdrop = backdrop,
                            config = config,
                            tintColor = CyanPulse,
                            tintAlpha = 0.28f,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("primary_glass_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = IceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (controls.buttonTapCount == 0) "Тест нажатия" else "Нажато: ${controls.buttonTapCount}",
                                style = MaterialTheme.typography.labelLarge,
                                color = IceWhite
                            )
                        }

                        LiquidButton(
                            onClick = {
                                onReset()
                                onShowToast("Оптика сброшена")
                            },
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.testTag("reset_glass_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Сброс",
                                tint = IceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Сброс",
                                style = MaterialTheme.typography.labelLarge,
                                color = IceWhite
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LiquidIconButton(
                            onClick = { onShowToast("Воспроизведение") },
                            icon = Icons.Rounded.PlayArrow,
                            contentDescription = "Играть",
                            backdrop = backdrop,
                            config = config,
                            tintColor = CyanPulse,
                            modifier = Modifier.testTag("icon_btn_play")
                        )
                        LiquidIconButton(
                            onClick = {
                                onUpdateControls { it.copy(isLiked = !it.isLiked) }
                            },
                            icon = Icons.Rounded.Favorite,
                            contentDescription = "Избранное",
                            backdrop = backdrop,
                            config = config,
                            tintColor = if (controls.isLiked) PrismMagenta else null,
                            iconTint = if (controls.isLiked) PrismMagenta else IceWhite,
                            modifier = Modifier.testTag("icon_btn_fav")
                        )
                        LiquidIconButton(
                            onClick = { onShowToast("Поделиться профилем") },
                            icon = Icons.Rounded.Share,
                            contentDescription = "Поделиться",
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.testTag("icon_btn_share")
                        )
                        LiquidIconButton(
                            onClick = { onShowToast("Калибровка оптики") },
                            icon = Icons.Rounded.Tune,
                            contentDescription = "Настройки",
                            backdrop = backdrop,
                            config = config,
                            modifier = Modifier.testTag("icon_btn_tune")
                        )
                    }
                }
            }
        }

        // Selectable Glass Chips
        item {
            LiquidGlassCard(
                backdrop = backdrop,
                config = config,
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle(
                        title = "Стеклянные чипы (LiquidChip)",
                        subtitle = "Множественный выбор с живой тонировкой"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val chipNames = listOf(
                            "Преломление",
                            "Спекуляр",
                            "Дисперсия",
                            "Глубина 3D",
                            "Матовость",
                            "Внутренняя тень"
                        )
                        chipNames.forEach { label ->
                            LiquidChip(
                                label = label,
                                selected = label in controls.selectedChips,
                                onClick = { onToggleChip(label) },
                                backdrop = backdrop,
                                config = config,
                                modifier = Modifier.testTag("chip_$label")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = IceWhite
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = IceWhite.copy(alpha = 0.72f)
        )
    }
}

@Composable
private fun ToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    backdrop: Backdrop,
    config: GlassConfig,
    accentColor: Color,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (checked) accentColor else IceWhite.copy(alpha = 0.65f),
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = IceWhite
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = IceWhite.copy(alpha = 0.68f)
            )
        }
        LiquidToggle(
            checked = checked,
            onCheckedChange = onCheckedChange,
            backdrop = backdrop,
            config = config,
            accentColor = accentColor,
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun SliderRow(
    label: String,
    valueLabel: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    backdrop: Backdrop,
    config: GlassConfig,
    accentColor: Color,
    tag: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = IceWhite
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor
            )
        }
        LiquidSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            backdrop = backdrop,
            config = config,
            accentColor = accentColor,
            modifier = Modifier.testTag(tag)
        )
    }
}

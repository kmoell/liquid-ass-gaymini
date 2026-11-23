package com.example.model

import androidx.compose.ui.graphics.Color

enum class LabTab(val label: String) {
    CONTROLS("Контролы"),
    LENS("Линза"),
    SURFACES("Сцены"),
    STUDIO("Шейдер")
}

enum class BackdropSource(val label: String, val subtitle: String) {
    AURORA("Аврора", "Жидкие волны"),
    PRISM("Призма", "Грани и цвет"),
    FLORA("Неон", "Капли и свет"),
    CALIBRATION("Сетка", "Оптический тест")
}

enum class GlassTintPreset(val label: String, val color: Color) {
    CRYSTAL("Кристалл", Color.White),
    CYAN_ICE("Лёд", Color(0xFF7DD3FC)),
    ROSE_QUARTZ("Кварц", Color(0xFFF9A8D4)),
    MINT_GLASS("Мята", Color(0xFF6EE7B7)),
    OBSIDIAN("Дымка", Color(0xFF0F172A))
}

enum class GlassPreset(val label: String) {
    CRYSTAL("Кристалл"),
    FROSTED("Матовый"),
    PRISM_RGB("Призма RGB"),
    DEEP_DROP("Капля"),
    CONCAVE("Вогнутая")
}

data class GlassConfig(
    val activePreset: GlassPreset? = GlassPreset.CRYSTAL,
    val blurRadiusDp: Float = 14f,
    val refractionHeightDp: Float = 14f,
    val refractionAmountDp: Float = 28f,
    val depthEffect: Boolean = true,
    val chromaticAberration: Boolean = true,
    val saturation: Float = 1.45f,
    val exposure: Float = 0.04f,
    val contrast: Float = 1.04f,
    val surfaceAlpha: Float = 0.14f,
    val highlightAlpha: Float = 0.55f,
    val shadowRadiusDp: Float = 14f,
    val innerShadowRadiusDp: Float = 6f,
    val cornerRadiusDp: Float = 28f,
    val tintPreset: GlassTintPreset = GlassTintPreset.CRYSTAL,
    val backdropSource: BackdropSource = BackdropSource.AURORA,
    val animateBackdrop: Boolean = false
)

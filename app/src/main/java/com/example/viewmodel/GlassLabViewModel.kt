package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.BackdropSource
import com.example.model.GlassConfig
import com.example.model.GlassPreset
import com.example.model.GlassTintPreset
import com.example.model.LabTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ControlsDemoState(
    val wifiEnabled: Boolean = true,
    val bluetoothEnabled: Boolean = true,
    val airdropEnabled: Boolean = false,
    val focusEnabled: Boolean = true,
    val prismShiftEnabled: Boolean = true,
    val spatialAudioEnabled: Boolean = false,
    val volumeLevel: Float = 0.72f,
    val brightnessLevel: Float = 0.58f,
    val bassBoostLevel: Float = 0.40f,
    val selectedSegment: Int = 1,
    val selectedChips: Set<String> = setOf("Преломление", "Спекуляр"),
    val buttonTapCount: Int = 0,
    val isPlaying: Boolean = true,
    val trackProgress: Float = 0.36f,
    val isLiked: Boolean = true,
    val ccBrightness: Float = 0.78f,
    val ccVolume: Float = 0.64f,
    val ccFlashlight: Boolean = false,
    val ccNightShift: Boolean = true,
    val showGlassDialog: Boolean = false,
    val toastMessage: String? = null
)

class GlassLabViewModel : ViewModel() {

    private val _config = MutableStateFlow(GlassConfig())
    val config: StateFlow<GlassConfig> = _config.asStateFlow()

    private val _activeTab = MutableStateFlow(LabTab.CONTROLS)
    val activeTab: StateFlow<LabTab> = _activeTab.asStateFlow()

    private val _controls = MutableStateFlow(ControlsDemoState())
    val controls: StateFlow<ControlsDemoState> = _controls.asStateFlow()

    fun selectTab(tab: LabTab) {
        _activeTab.value = tab
    }

    fun updateConfig(transform: (GlassConfig) -> GlassConfig) {
        _config.update { current ->
            transform(current).copy(activePreset = null)
        }
    }

    fun setBackdropSource(source: BackdropSource) {
        _config.update { it.copy(backdropSource = source) }
    }

    fun toggleBackdropAnimation(enabled: Boolean) {
        _config.update { it.copy(animateBackdrop = enabled) }
    }

    fun setTintPreset(tint: GlassTintPreset) {
        _config.update { it.copy(tintPreset = tint, activePreset = null) }
    }

    fun applyPreset(preset: GlassPreset) {
        _config.update { current ->
            when (preset) {
                GlassPreset.CRYSTAL -> current.copy(
                    activePreset = preset,
                    blurRadiusDp = 14f,
                    refractionHeightDp = 14f,
                    refractionAmountDp = 28f,
                    depthEffect = true,
                    chromaticAberration = true,
                    saturation = 1.45f,
                    exposure = 0.04f,
                    contrast = 1.04f,
                    surfaceAlpha = 0.14f,
                    highlightAlpha = 0.55f,
                    shadowRadiusDp = 14f,
                    innerShadowRadiusDp = 6f,
                    cornerRadiusDp = 28f,
                    tintPreset = GlassTintPreset.CRYSTAL
                )
                GlassPreset.FROSTED -> current.copy(
                    activePreset = preset,
                    blurRadiusDp = 32f,
                    refractionHeightDp = 10f,
                    refractionAmountDp = 14f,
                    depthEffect = false,
                    chromaticAberration = false,
                    saturation = 1.25f,
                    exposure = 0.08f,
                    contrast = 0.98f,
                    surfaceAlpha = 0.28f,
                    highlightAlpha = 0.42f,
                    shadowRadiusDp = 18f,
                    innerShadowRadiusDp = 10f,
                    cornerRadiusDp = 24f,
                    tintPreset = GlassTintPreset.CYAN_ICE
                )
                GlassPreset.PRISM_RGB -> current.copy(
                    activePreset = preset,
                    blurRadiusDp = 6f,
                    refractionHeightDp = 22f,
                    refractionAmountDp = 46f,
                    depthEffect = true,
                    chromaticAberration = true,
                    saturation = 1.85f,
                    exposure = 0.02f,
                    contrast = 1.12f,
                    surfaceAlpha = 0.08f,
                    highlightAlpha = 0.72f,
                    shadowRadiusDp = 16f,
                    innerShadowRadiusDp = 4f,
                    cornerRadiusDp = 32f,
                    tintPreset = GlassTintPreset.CRYSTAL
                )
                GlassPreset.DEEP_DROP -> current.copy(
                    activePreset = preset,
                    blurRadiusDp = 2f,
                    refractionHeightDp = 28f,
                    refractionAmountDp = 52f,
                    depthEffect = true,
                    chromaticAberration = true,
                    saturation = 1.6f,
                    exposure = 0.0f,
                    contrast = 1.08f,
                    surfaceAlpha = 0.06f,
                    highlightAlpha = 0.80f,
                    shadowRadiusDp = 22f,
                    innerShadowRadiusDp = 8f,
                    cornerRadiusDp = 36f,
                    tintPreset = GlassTintPreset.CRYSTAL
                )
                GlassPreset.CONCAVE -> current.copy(
                    activePreset = preset,
                    blurRadiusDp = 10f,
                    refractionHeightDp = 18f,
                    refractionAmountDp = -38f,
                    depthEffect = true,
                    chromaticAberration = true,
                    saturation = 1.35f,
                    exposure = -0.04f,
                    contrast = 1.06f,
                    surfaceAlpha = 0.16f,
                    highlightAlpha = 0.48f,
                    shadowRadiusDp = 12f,
                    innerShadowRadiusDp = 14f,
                    cornerRadiusDp = 26f,
                    tintPreset = GlassTintPreset.ROSE_QUARTZ
                )
            }
        }
    }

    fun resetDefaults() {
        applyPreset(GlassPreset.CRYSTAL)
    }

    fun updateControls(transform: (ControlsDemoState) -> ControlsDemoState) {
        _controls.update(transform)
    }

    fun toggleChip(chip: String) {
        _controls.update { state ->
            val next = if (chip in state.selectedChips) {
                state.selectedChips - chip
            } else {
                state.selectedChips + chip
            }
            state.copy(selectedChips = next)
        }
    }

    fun triggerButtonTap() {
        _controls.update { it.copy(buttonTapCount = it.buttonTapCount + 1) }
    }

    fun showToast(message: String) {
        _controls.update { it.copy(toastMessage = message) }
    }

    fun dismissToast() {
        _controls.update { it.copy(toastMessage = null) }
    }

    fun setShowDialog(show: Boolean) {
        _controls.update { it.copy(showGlassDialog = show) }
    }
}

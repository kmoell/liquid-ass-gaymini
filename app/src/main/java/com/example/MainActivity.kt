package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GlassPreset
import com.example.model.LabTab
import com.example.ui.components.LiquidBottomTabs
import com.example.ui.components.LiquidButton
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.RichBackdropCanvas
import com.example.ui.components.liquidGlassSurface
import com.example.ui.screens.ControlsScreen
import com.example.ui.screens.LensLabScreen
import com.example.ui.screens.ShaderStudioScreen
import com.example.ui.screens.SurfacesScreen
import com.example.ui.theme.CyanPulse
import com.example.ui.theme.GlassLabTheme
import com.example.ui.theme.IceWhite
import com.example.ui.theme.MintGlow
import com.example.viewmodel.GlassLabViewModel
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            GlassLabTheme {
                GlassLabApp()
            }
        }
    }
}

@Composable
fun GlassLabApp(
    viewModel: GlassLabViewModel = viewModel()
) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val controls by viewModel.controls.collectAsStateWithLifecycle()

    val backdrop = rememberLayerBackdrop()

    val statusPadding = WindowInsets.statusBars.asPaddingValues()
    val navPadding = WindowInsets.navigationBars.asPaddingValues()

    val screenContentPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = statusPadding.calculateTopPadding() + 14.dp,
        bottom = navPadding.calculateBottomPadding() + 104.dp
    )

    BackHandler(enabled = controls.showGlassDialog || activeTab != LabTab.CONTROLS) {
        if (controls.showGlassDialog) {
            viewModel.setShowDialog(false)
        } else {
            viewModel.selectTab(LabTab.CONTROLS)
        }
    }

    LaunchedEffect(controls.toastMessage) {
        if (controls.toastMessage != null) {
            delay(2200)
            viewModel.dismissToast()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Live Rich Backdrop Layer captured by AndroidLiquidGlass
        RichBackdropCanvas(
            source = config.backdropSource,
            animate = config.animateBackdrop,
            modifier = Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
        )

        // 2. Active Screen Layer
        Crossfade(
            targetState = activeTab,
            label = "tab_crossfade",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                LabTab.CONTROLS -> ControlsScreen(
                    config = config,
                    controls = controls,
                    backdrop = backdrop,
                    contentPadding = screenContentPadding,
                    onSelectPreset = viewModel::applyPreset,
                    onSelectBackdrop = viewModel::setBackdropSource,
                    onUpdateConfig = viewModel::updateConfig,
                    onUpdateControls = viewModel::updateControls,
                    onToggleChip = viewModel::toggleChip,
                    onButtonTap = viewModel::triggerButtonTap,
                    onReset = viewModel::resetDefaults,
                    onShowToast = viewModel::showToast
                )

                LabTab.LENS -> LensLabScreen(
                    config = config,
                    backdrop = backdrop,
                    contentPadding = screenContentPadding,
                    onSelectBackdrop = viewModel::setBackdropSource,
                    onUpdateConfig = viewModel::updateConfig
                )

                LabTab.SURFACES -> SurfacesScreen(
                    config = config,
                    controls = controls,
                    backdrop = backdrop,
                    contentPadding = screenContentPadding,
                    onUpdateControls = viewModel::updateControls,
                    onOpenDialog = { viewModel.setShowDialog(true) },
                    onShowToast = viewModel::showToast
                )

                LabTab.STUDIO -> ShaderStudioScreen(
                    config = config,
                    backdrop = backdrop,
                    contentPadding = screenContentPadding,
                    onUpdateConfig = viewModel::updateConfig,
                    onSelectTint = viewModel::setTintPreset,
                    onReset = viewModel::resetDefaults
                )
            }
        }

        // 3. Floating Liquid Glass Bottom Navigation Bar
        LiquidBottomTabs(
            selectedTab = activeTab,
            onTabSelected = viewModel::selectTab,
            backdrop = backdrop,
            config = config,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 4. Floating Liquid Glass Toast Notification
        AnimatedVisibility(
            visible = controls.toastMessage != null,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)
            ) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = statusPadding.calculateTopPadding() + 12.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .liquidGlassSurface(
                        backdrop = backdrop,
                        config = config,
                        shape = { CircleShape },
                        tintColor = CyanPulse,
                        tintAlpha = 0.24f
                    )
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MintGlow,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = controls.toastMessage.orEmpty(),
                    style = MaterialTheme.typography.labelLarge,
                    color = IceWhite
                )
            }
        }

        // 5. Floating Liquid Glass Modal Dialog Overlay
        AnimatedVisibility(
            visible = controls.showGlassDialog,
            enter = fadeIn() + scaleIn(initialScale = 0.92f),
            exit = fadeOut() + scaleOut(targetScale = 0.92f),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.36f))
                    .pointerInput(Unit) {
                        detectTapGestures { viewModel.setShowDialog(false) }
                    }
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidGlassCard(
                    backdrop = backdrop,
                    config = config,
                    cornerRadius = 32.dp,
                    contentPadding = 22.dp,
                    modifier = Modifier
                        .widthIn(max = 420.dp)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures { /* Consume tap inside dialog */ }
                        }
                        .testTag("liquid_glass_dialog")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = CyanPulse,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Стеклянный диалог",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = IceWhite
                            )
                        }

                        Text(
                            text = "Это модальное окно рендерит преломление и размытие прямо поверх активного фона в реальном времени.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = IceWhite.copy(alpha = 0.85f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LiquidButton(
                                onClick = {
                                    viewModel.applyPreset(GlassPreset.PRISM_RGB)
                                    viewModel.showToast("Включён пресет Призма RGB")
                                },
                                backdrop = backdrop,
                                config = config,
                                tintColor = CyanPulse,
                                tintAlpha = 0.28f,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dialog_prism_button")
                            ) {
                                Text(
                                    text = "Призма RGB",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = IceWhite
                                )
                            }

                            LiquidButton(
                                onClick = { viewModel.setShowDialog(false) },
                                backdrop = backdrop,
                                config = config,
                                modifier = Modifier.testTag("dialog_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Закрыть",
                                    tint = IceWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Закрыть",
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
}

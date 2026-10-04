package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.AetherViewModel
import com.example.ui.AppHudTab
import com.example.ui.components.HologramHeader
import com.example.ui.components.HologramNavigationBar
import com.example.ui.screens.DailyScheduleScreen
import com.example.ui.screens.HoloAssistantScreen
import com.example.ui.screens.ScriptIdeScreen
import com.example.ui.screens.SmartHomeScreen
import com.example.ui.screens.TermuxDashboardScreen
import com.example.ui.theme.HoloVoidBlack
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: AetherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AetherApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AetherApp(viewModel: AetherViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isServerRunning by viewModel.termuxServer.isRunning.collectAsState()
    val serverPort by viewModel.termuxServer.port.collectAsState()
    val isHighThinking by viewModel.isHighThinkingEnabled.collectAsState()
    val expression by viewModel.voiceManager.expression.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle back button: navigate to Home Orb tab if in sub-screen
    BackHandler(enabled = currentTab != AppHudTab.ASSISTANT_ORB) {
        viewModel.setTab(AppHudTab.ASSISTANT_ORB)
    }

    // Gesture navigation swipe listener across tabs
    val tabsList = AppHudTab.values()
    val draggableState = rememberDraggableState { delta ->
        if (delta < -30) {
            // Swipe Left -> Next Tab
            val nextIndex = (currentTab.ordinal + 1).coerceAtMost(tabsList.lastIndex)
            if (nextIndex != currentTab.ordinal) {
                viewModel.setTab(tabsList[nextIndex])
            }
        } else if (delta > 30) {
            // Swipe Right -> Prev Tab
            val prevIndex = (currentTab.ordinal - 1).coerceAtLeast(0)
            if (prevIndex != currentTab.ordinal) {
                viewModel.setTab(tabsList[prevIndex])
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(HoloVoidBlack)
            .testTag("aether_main_scaffold"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(modifier = Modifier.padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())) {
                HologramHeader(
                    isServerRunning = isServerRunning,
                    serverPort = serverPort,
                    isHighThinking = isHighThinking,
                    expression = expression,
                    isSpeaking = isSpeaking,
                    isListening = isListening,
                    onServerClick = {
                        viewModel.setTab(AppHudTab.TERMUX_DASHBOARD)
                    },
                    onThinkingClick = {
                        viewModel.toggleHighThinking()
                        scope.launch {
                            val status = if (!isHighThinking) "HIGH THINKING (Gemini 3.1 Pro) ACTIVE" else "Standard thinking mode set"
                            snackbarHostState.showSnackbar(status)
                        }
                    }
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())) {
                HologramNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(HoloVoidBlack)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal
                )
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    AppHudTab.ASSISTANT_ORB -> HoloAssistantScreen(viewModel = viewModel)
                    AppHudTab.SMART_HOME -> SmartHomeScreen(viewModel = viewModel)
                    AppHudTab.SCHEDULE -> DailyScheduleScreen(viewModel = viewModel)
                    AppHudTab.SCRIPT_IDE -> ScriptIdeScreen(viewModel = viewModel)
                    AppHudTab.TERMUX_DASHBOARD -> TermuxDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

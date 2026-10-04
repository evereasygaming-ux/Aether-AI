package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AetherViewModel
import com.example.ui.components.HolographicOrb3D
import com.example.ui.theme.HoloAlertRed
import com.example.ui.theme.HoloAmber
import com.example.ui.theme.HoloBlue
import com.example.ui.theme.HoloBorderGlow
import com.example.ui.theme.HoloCardElevated
import com.example.ui.theme.HoloCardSurface
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.HoloDarkSurface
import com.example.ui.theme.HoloGreen
import com.example.ui.theme.HoloViolet
import com.example.ui.theme.HoloVoidBlack
import com.example.ui.theme.TextHoloDim
import com.example.ui.theme.TextHoloPrimary
import com.example.ui.theme.TextHoloSecondary
import com.example.voice.OrbExpression

@Composable
fun HoloAssistantScreen(
    viewModel: AetherViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.voiceManager.expression.collectAsState()
    val audioWave by viewModel.voiceManager.audioWave.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val isHighThinking by viewModel.isHighThinkingEnabled.collectAsState()
    val lastResponse by viewModel.lastAiResponse.collectAsState()
    val conversationHistory by viewModel.conversationHistory.collectAsState()
    val currentQuery by viewModel.currentQuery.collectAsState()

    var showThinkingTrail by remember { mutableStateOf(true) }

    val quickMacros = listOf(
        "Run Termux diagnostics",
        "Set Living Room lights to Cyan",
        "Initiate night lockdown",
        "Generate daily briefing",
        "Deploy quantum code sync"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HoloVoidBlack)
            .testTag("holo_assistant_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Centerpiece: 3D Holographic Animated Orb
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HolographicOrb3D(
                            modifier = Modifier.fillMaxSize(),
                            expression = expression,
                            audioWave = audioWave,
                            isSpeaking = isSpeaking,
                            isListening = isListening,
                            onOrbTap = {
                                if (isListening) {
                                    viewModel.voiceManager.stopListening()
                                } else {
                                    viewModel.voiceManager.startListeningSimulation { text ->
                                        viewModel.submitVoiceQuery(text)
                                    }
                                }
                            },
                            onOrbDoubleTap = {
                                viewModel.playDailyBriefing()
                            }
                        )
                    }

                    // Voice / Audio status pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(HoloCardSurface)
                            .border(1.dp, HoloBorderGlow, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                isSpeaking -> Icons.AutoMirrored.Filled.VolumeUp
                                isListening -> Icons.Default.Mic
                                isAiThinking -> Icons.Default.Psychology
                                else -> Icons.Default.GraphicEq
                            },
                            contentDescription = null,
                            tint = when {
                                isSpeaking -> HoloViolet
                                isListening -> HoloCyan
                                isAiThinking -> HoloAmber
                                else -> TextHoloDim
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                isAiThinking -> "GEMINI 3.1 PRO (HIGH THINKING)..."
                                isSpeaking -> "SYNTHESIZING VOICE..."
                                isListening -> "LISTENING TO FREQUENCY..."
                                else -> "DRAG TO ROTATE 3D • TAP TO SPEAK"
                            },
                            color = when {
                                isAiThinking -> HoloAmber
                                isSpeaking -> HoloViolet
                                isListening -> HoloCyan
                                else -> TextHoloSecondary
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // 2. Gemini 3.1 Pro High-Thinking Reasoning Trail Panel
            if (lastResponse?.thoughtTrail?.isNotBlank() == true) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(HoloCardElevated)
                            .border(1.dp, HoloViolet.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .testTag("ai_thought_trail_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showThinkingTrail = !showThinkingTrail },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = HoloViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GEMINI 3.1 PRO • HIGH-THINKING TRAIL",
                                    color = HoloViolet,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = if (showThinkingTrail) "HIDE" else "EXPAND",
                                color = TextHoloDim,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        AnimatedVisibility(visible = showThinkingTrail) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = lastResponse!!.thoughtTrail,
                                    color = TextHoloSecondary,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 3. Quick Voice Macro Action Chips
            item {
                Column {
                    Text(
                        text = "VOICE AUTOMATION MACROS",
                        color = TextHoloDim,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickMacros.forEach { macro ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HoloCardSurface)
                                    .border(1.dp, HoloBorderGlow, RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.submitVoiceQuery(macro)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("macro_chip_${macro.take(10)}")
                            ) {
                                Text(
                                    text = macro,
                                    color = HoloCyan,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // 4. Conversation Stream
            item {
                Text(
                    text = "NEURAL LOG STREAM",
                    color = TextHoloDim,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(conversationHistory.takeLast(6)) { (sender, message) ->
                val isAether = sender == "Aether" || sender == "System"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAether) Arrangement.Start else Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAether) HoloCardSurface else HoloCardElevated)
                            .border(
                                1.dp,
                                if (isAether) HoloCyan.copy(alpha = 0.35f) else HoloViolet.copy(alpha = 0.35f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = sender.uppercase(),
                                color = if (isAether) HoloCyan else HoloViolet,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message,
                                color = TextHoloPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Bottom Voice & Query Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(HoloDarkSurface)
                .border(1.dp, HoloBorderGlow)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Mic Button (Triggers speech recognition / voice animation)
            IconButton(
                onClick = {
                    if (isListening) {
                        viewModel.voiceManager.stopListening()
                    } else {
                        viewModel.voiceManager.startListeningSimulation { query ->
                            viewModel.submitVoiceQuery(query)
                        }
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isListening) HoloCyan else HoloCardElevated)
                    .border(1.dp, if (isListening) HoloCyan else HoloBorderGlow, CircleShape)
                    .testTag("mic_toggle_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = if (isListening) HoloVoidBlack else HoloCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Query Text Field
            OutlinedTextField(
                value = currentQuery,
                onValueChange = { viewModel.updateQuery(it) },
                placeholder = {
                    Text(
                        text = "Speak or enter command...",
                        color = TextHoloDim,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("query_text_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HoloCyan,
                    unfocusedBorderColor = HoloBorderGlow,
                    focusedTextColor = TextHoloPrimary,
                    unfocusedTextColor = TextHoloPrimary,
                    cursorColor = HoloCyan
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    viewModel.submitVoiceQuery(currentQuery)
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Submit Button
            IconButton(
                onClick = {
                    viewModel.submitVoiceQuery(currentQuery)
                },
                enabled = currentQuery.isNotBlank() && !isAiThinking,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (currentQuery.isNotBlank()) HoloViolet else HoloCardSurface)
                    .testTag("send_query_button")
            ) {
                if (isAiThinking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = HoloCyan,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (currentQuery.isNotBlank()) Color.White else TextHoloDim,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

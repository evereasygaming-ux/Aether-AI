package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HoloAlertRed
import com.example.ui.theme.HoloAmber
import com.example.ui.theme.HoloBorderGlow
import com.example.ui.theme.HoloCardSurface
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.HoloDarkSurface
import com.example.ui.theme.HoloGreen
import com.example.ui.theme.HoloViolet
import com.example.ui.theme.TextHoloDim
import com.example.ui.theme.TextHoloPrimary
import com.example.ui.theme.TextHoloSecondary
import com.example.voice.OrbExpression

@Composable
fun HologramHeader(
    modifier: Modifier = Modifier,
    isServerRunning: Boolean,
    serverPort: Int,
    isHighThinking: Boolean,
    expression: OrbExpression,
    isSpeaking: Boolean,
    isListening: Boolean,
    onServerClick: () -> Unit,
    onThinkingClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HoloDarkSurface,
                        HoloDarkSurface.copy(alpha = 0.85f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("hologram_header")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding with holographic badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(HoloCardSurface)
                        .border(1.dp, HoloCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Holo Sensor",
                        tint = HoloCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AETHER",
                            color = TextHoloPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3D OS",
                            color = HoloCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .background(HoloCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Text(
                        text = "CORE: ${expression.name}",
                        color = when (expression) {
                            OrbExpression.ALERT -> HoloAlertRed
                            OrbExpression.HAPPY -> HoloGreen
                            OrbExpression.THINKING -> HoloAmber
                            OrbExpression.SPEAKING -> HoloViolet
                            else -> HoloCyan
                        },
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Status badges: Termux localhost + High Thinking mode
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Termux Server status pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(HoloCardSurface)
                        .border(
                            1.dp,
                            if (isServerRunning) HoloGreen.copy(alpha = 0.6f) else HoloAlertRed.copy(alpha = 0.6f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onServerClick() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("termux_server_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isServerRunning) HoloGreen else HoloAlertRed)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = ":$serverPort",
                        color = if (isServerRunning) TextHoloPrimary else TextHoloDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Thinking Mode Toggle Pill (gemini-3.1-pro-preview HIGH)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(HoloCardSurface)
                        .border(
                            1.dp,
                            if (isHighThinking) HoloViolet.copy(alpha = 0.8f) else HoloBorderGlow,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onThinkingClick() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("thinking_mode_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Thinking mode",
                        tint = if (isHighThinking) HoloViolet else TextHoloDim,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHighThinking) "HIGH THINK" else "STANDARD",
                        color = if (isHighThinking) HoloViolet else TextHoloSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

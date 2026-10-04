package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Terminal
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
import com.example.ui.AppHudTab
import com.example.ui.theme.HoloBorderGlow
import com.example.ui.theme.HoloCardSurface
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.HoloDarkSurface
import com.example.ui.theme.HoloViolet
import com.example.ui.theme.TextHoloDim
import com.example.ui.theme.TextHoloPrimary

@Composable
fun HologramNavigationBar(
    currentTab: AppHudTab,
    onTabSelected: (AppHudTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        HoloDarkSurface.copy(alpha = 0.95f),
                        Color(0xFF04070E)
                    )
                )
            )
            .border(1.dp, HoloBorderGlow)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = listOf(
            Triple(AppHudTab.ASSISTANT_ORB, "ORB 3D", Icons.Default.Sensors),
            Triple(AppHudTab.SMART_HOME, "HOME", Icons.Default.Lightbulb),
            Triple(AppHudTab.SCHEDULE, "SCHEDULE", Icons.Default.CalendarToday),
            Triple(AppHudTab.SCRIPT_IDE, "IDE", Icons.Default.Terminal),
            Triple(AppHudTab.TERMUX_DASHBOARD, "TERMUX", Icons.Default.Hub)
        )

        tabs.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) HoloCyan.copy(alpha = 0.12f) else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("nav_tab_${tab.name}"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) HoloCyan else TextHoloDim,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = label,
                        color = if (isSelected) HoloCyan else TextHoloDim,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

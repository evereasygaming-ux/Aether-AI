package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScheduleItem
import com.example.ui.AetherViewModel
import com.example.ui.theme.HoloAlertRed
import com.example.ui.theme.HoloAmber
import com.example.ui.theme.HoloBorderGlow
import com.example.ui.theme.HoloCardElevated
import com.example.ui.theme.HoloCardSurface
import com.example.ui.theme.HoloCyan
import com.example.ui.theme.HoloDarkSurface
import com.example.ui.theme.HoloGreen
import com.example.ui.theme.HoloTeal
import com.example.ui.theme.HoloViolet
import com.example.ui.theme.HoloVoidBlack
import com.example.ui.theme.TextHoloDim
import com.example.ui.theme.TextHoloPrimary
import com.example.ui.theme.TextHoloSecondary
import kotlinx.coroutines.launch

@Composable
fun DailyScheduleScreen(
    viewModel: AetherViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsState()
    var selectedCategory by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "ROUTINE", "AUTOMATION", "SYNC", "SECURITY")
    val filteredSchedules = if (selectedCategory == "ALL") schedules else schedules.filter { it.category.equals(selectedCategory, ignoreCase = true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HoloVoidBlack)
            .testTag("daily_schedule_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "QUANTUM DAILY SCHEDULE",
                        color = HoloCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "VOICE-AUTOMATED ROUTINES & TIMELINE EXECUTION",
                        color = TextHoloSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Morning Briefing Interactive Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(HoloCardElevated, HoloCardSurface)
                            )
                        )
                        .border(1.dp, HoloViolet.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                        .testTag("morning_briefing_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = HoloViolet,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AETHER MORNING INTEL BRIEFING",
                                    color = HoloViolet,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            IconButton(
                                onClick = { viewModel.playDailyBriefing() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(HoloViolet.copy(alpha = 0.2f))
                                    .testTag("play_briefing_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Briefing",
                                    tint = HoloViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Synthesizes device status, upcoming calendar routines, and Termux socket health via voice TTS.",
                            color = TextHoloSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Category Filter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) HoloCyan.copy(alpha = 0.2f) else HoloCardSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) HoloCyan else HoloBorderGlow,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) HoloCyan else TextHoloDim,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Schedule Items
            items(filteredSchedules, key = { it.id }) { item ->
                ScheduleItemCard(
                    schedule = item,
                    onToggle = { viewModel.toggleSchedule(item.id, item.isEnabled) },
                    onDelete = {
                        viewModel.deleteSchedule(item.id)
                    },
                    onTriggerNow = {
                        viewModel.submitVoiceQuery("Trigger routine: ${item.title}")
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

        // Add Schedule FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_schedule_fab"),
            containerColor = HoloCyan,
            contentColor = HoloVoidBlack
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Schedule")
        }
    }

    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, time, category, trigger ->
                viewModel.addSchedule(title, time, category, trigger)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ScheduleItemCard(
    schedule: ScheduleItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTriggerNow: () -> Unit
) {
    val isEnabled = schedule.isEnabled

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HoloCardSurface)
            .border(
                1.dp,
                if (isEnabled) HoloCyan.copy(alpha = 0.45f) else HoloBorderGlow,
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
            .testTag("schedule_card_${schedule.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isEnabled) HoloCyan.copy(alpha = 0.15f) else HoloDarkSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = schedule.time,
                            color = if (isEnabled) HoloCyan else TextHoloDim,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = schedule.category,
                        color = when (schedule.category) {
                            "SECURITY" -> HoloAlertRed
                            "AUTOMATION" -> HoloViolet
                            "SYNC" -> HoloGreen
                            else -> HoloTeal
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onTriggerNow, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Trigger Now",
                            tint = HoloCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextHoloDim,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HoloCyan,
                            checkedTrackColor = HoloCyan.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextHoloDim,
                            uncheckedTrackColor = HoloDarkSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = schedule.title,
                color = TextHoloPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Voice command trigger tag
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(HoloDarkSurface)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = HoloCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "VOICE: \"${schedule.voiceTrigger}\"",
                    color = TextHoloSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, time: String, category: String, trigger: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("08:00 AM") }
    var category by remember { mutableStateOf("ROUTINE") }
    var trigger by remember { mutableStateOf("") }

    val categories = listOf("ROUTINE", "AUTOMATION", "SYNC", "SECURITY")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "CREATE AUTOMATED SCHEDULE",
                color = HoloCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine Title (e.g. Quantum Standup)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g. 09:30 AM)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text("Voice Trigger (e.g. Activate morning matrix)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { c ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (category == c) HoloCyan else HoloCardSurface)
                                .clickable { category = c }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = c,
                                color = if (category == c) HoloVoidBlack else TextHoloPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, time, category, trigger.ifBlank { "run routine $title" })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HoloCyan, contentColor = HoloVoidBlack)
            ) {
                Text("SAVE SCHEDULE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextHoloDim)
            }
        },
        containerColor = HoloDarkSurface,
        shape = RoundedCornerShape(14.dp)
    )
}

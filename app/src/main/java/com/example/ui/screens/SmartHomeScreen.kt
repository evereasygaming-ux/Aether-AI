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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SmartDevice
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

@Composable
fun SmartHomeScreen(
    viewModel: AetherViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsState()
    var selectedRoom by remember { mutableStateOf("ALL") }
    var showAddDeviceDialog by remember { mutableStateOf(false) }

    val rooms = listOf("ALL", "Living Room", "Quantum Lab", "Central HVAC", "Front Entry", "Studio")
    val scenes = listOf("Cinema Mode", "Night Routine", "Focus Matrix", "Party Grid")

    val filteredDevices = if (selectedRoom == "ALL") devices else devices.filter { it.room.equals(selectedRoom, ignoreCase = true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HoloVoidBlack)
            .testTag("smart_home_screen")
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
                        text = "HOLOGRAPHIC SMART GRID",
                        color = HoloCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "${devices.count { it.isOn }} ACTIVE DEVICES • ${devices.sumOf { it.powerWatts.toDouble() }.toInt()}W CURRENT CONSUMPTION",
                        color = TextHoloSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick Holographic Automation Scenes
            item {
                Column {
                    Text(
                        text = "QUICK HOLOGRAPHIC SCENES",
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
                        scenes.forEach { scene ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(HoloCardSurface)
                                    .border(1.dp, HoloViolet.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .clickable { viewModel.activateScene(scene) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("scene_btn_${scene.take(6)}")
                            ) {
                                Text(
                                    text = scene,
                                    color = HoloViolet,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Room Filter Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rooms.forEach { room ->
                        val isSelected = selectedRoom == room
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) HoloCyan.copy(alpha = 0.2f) else HoloCardSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) HoloCyan else HoloBorderGlow,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedRoom = room }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = room.uppercase(),
                                color = if (isSelected) HoloCyan else TextHoloDim,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Devices List
            items(filteredDevices, key = { it.id }) { device ->
                SmartDeviceItemCard(
                    device = device,
                    onToggle = { viewModel.toggleDevice(device.id, device.isOn) },
                    onValueChange = { viewModel.updateDeviceValue(device.id, it) }
                )
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

        // Add Device Floating Action Button
        FloatingActionButton(
            onClick = { showAddDeviceDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_device_fab"),
            containerColor = HoloCyan,
            contentColor = HoloVoidBlack
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Smart Device")
        }
    }

    if (showAddDeviceDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDeviceDialog = false },
            onAdd = { name, room, type ->
                viewModel.addSmartDevice(name, room, type)
                showAddDeviceDialog = false
            }
        )
    }
}

@Composable
fun SmartDeviceItemCard(
    device: SmartDevice,
    onToggle: () -> Unit,
    onValueChange: (Float) -> Unit
) {
    val isOnline = device.isOnline
    val isOn = device.isOn

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HoloCardSurface)
            .border(
                1.dp,
                if (isOn) HoloCyan.copy(alpha = 0.5f) else HoloBorderGlow,
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
            .testTag("device_card_${device.id}")
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isOn) HoloCyan.copy(alpha = 0.2f) else HoloDarkSurface)
                            .border(1.dp, if (isOn) HoloCyan else HoloBorderGlow, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (device.type) {
                                "LIGHT" -> Icons.Default.Lightbulb
                                "THERMOSTAT" -> Icons.Default.Thermostat
                                "PLUG" -> Icons.Default.Power
                                "LOCK" -> if (isOn) Icons.Default.Lock else Icons.Default.LockOpen
                                "SPEAKER" -> Icons.Default.Speaker
                                else -> Icons.Default.Power
                            },
                            contentDescription = null,
                            tint = if (isOn) HoloCyan else TextHoloDim,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = device.name,
                            color = TextHoloPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${device.room.uppercase()} • ${device.powerWatts}W",
                            color = TextHoloDim,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Switch(
                    checked = isOn,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = HoloCyan,
                        checkedTrackColor = HoloCyan.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextHoloDim,
                        uncheckedTrackColor = HoloDarkSurface
                    ),
                    modifier = Modifier.testTag("switch_${device.id}")
                )
            }

            // Sub-controls based on type
            if (isOn) {
                Spacer(modifier = Modifier.height(10.dp))
                when (device.type) {
                    "LIGHT" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "BRIGHTNESS",
                                color = TextHoloDim,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(75.dp)
                            )
                            Slider(
                                value = device.value,
                                onValueChange = onValueChange,
                                valueRange = 0f..100f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = HoloCyan,
                                    activeTrackColor = HoloCyan,
                                    inactiveTrackColor = HoloDarkSurface
                                )
                            )
                            Text(
                                text = "${device.value.toInt()}%",
                                color = HoloCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }

                    "THERMOSTAT" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "TEMPERATURE",
                                color = TextHoloDim,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onValueChange((device.value - 0.5f).coerceAtLeast(16f)) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("-", color = HoloCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = String.format("%.1f°C", device.value),
                                    color = HoloTeal,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { onValueChange((device.value + 0.5f).coerceAtMost(30f)) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Text("+", color = HoloCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    "LOCK" -> {
                        Text(
                            text = if (isOn) "VAULT STATUS: SECURELY ARMED" else "VAULT STATUS: UNLOCKED",
                            color = if (isOn) HoloGreen else HoloAlertRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    "SPEAKER" -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "VOLUME",
                                color = TextHoloDim,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(60.dp)
                            )
                            Slider(
                                value = device.value,
                                onValueChange = onValueChange,
                                valueRange = 0f..100f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = HoloViolet,
                                    activeTrackColor = HoloViolet,
                                    inactiveTrackColor = HoloDarkSurface
                                )
                            )
                            Text(
                                text = "${device.value.toInt()}%",
                                color = HoloViolet,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, room: String, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("Living Room") }
    var type by remember { mutableStateOf("LIGHT") }

    val types = listOf("LIGHT", "THERMOSTAT", "PLUG", "LOCK", "SPEAKER")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ENROLL NEW SMART DEVICE",
                color = HoloCyan,
                fontFamily = FontFamily.Monospace,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Device Name (e.g. Ambient Wall)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room (e.g. Quantum Lab)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoloCyan,
                        focusedTextColor = TextHoloPrimary,
                        unfocusedTextColor = TextHoloPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "DEVICE TYPE:",
                    color = TextHoloDim,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.forEach { t ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (type == t) HoloCyan else HoloCardSurface)
                                .clickable { type = t }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = t,
                                color = if (type == t) HoloVoidBlack else TextHoloPrimary,
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
                    if (name.isNotBlank()) onAdd(name, room, type)
                },
                colors = ButtonDefaults.buttonColors(containerColor = HoloCyan, contentColor = HoloVoidBlack)
            ) {
                Text("CONNECT DEVICE")
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

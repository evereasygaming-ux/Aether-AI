package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_devices")
data class SmartDevice(
    @PrimaryKey val id: String,
    val name: String,
    val room: String,
    val type: String, // LIGHT, THERMOSTAT, PLUG, LOCK, SPEAKER, SHADE
    val isOn: Boolean,
    val value: Float, // Brightness (0-100), Temp in C, etc.
    val colorHex: String,
    val isOnline: Boolean,
    val powerWatts: Float = 0f
)

@Entity(tableName = "schedules")
data class ScheduleItem(
    @PrimaryKey val id: String,
    val title: String,
    val time: String,
    val category: String, // ROUTINE, AUTOMATION, MEETING, SYNC, SECURITY
    val days: String,
    val voiceTrigger: String,
    val actionPayload: String,
    val isEnabled: Boolean
)

@Entity(tableName = "voice_scripts")
data class VoiceScript(
    @PrimaryKey val id: String,
    val title: String,
    val language: String, // PYTHON, BASH, JAVASCRIPT
    val triggerPhrase: String,
    val code: String,
    val description: String,
    val lastRunStatus: String = "IDLE", // IDLE, RUNNING, SUCCESS, FAILED
    val lastRunOutput: String = "",
    val lastRunTime: Long = 0L
)

@Entity(tableName = "server_logs")
data class ServerLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val endpoint: String,
    val method: String,
    val status: Int,
    val details: String
)

data class ConnectedDeviceNode(
    val id: String,
    val name: String,
    val type: String, // "TERMUX_NODE", "SMART_DISPLAY", "NEURAL_HUB", "WATCH", "DESKTOP"
    val address: String,
    val pingMs: Int,
    val isOnline: Boolean,
    val syncStatus: String
)

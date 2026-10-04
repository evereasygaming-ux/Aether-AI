package com.example.data.repository

import com.example.data.local.AetherDatabase
import com.example.data.model.ConnectedDeviceNode
import com.example.data.model.ScheduleItem
import com.example.data.model.ServerLog
import com.example.data.model.SmartDevice
import com.example.data.model.VoiceScript
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AetherRepository(private val database: AetherDatabase) {
    val devices: Flow<List<SmartDevice>> = database.smartDeviceDao().getAllDevices()
    val schedules: Flow<List<ScheduleItem>> = database.scheduleDao().getAllSchedules()
    val scripts: Flow<List<VoiceScript>> = database.voiceScriptDao().getAllScripts()
    val serverLogs: Flow<List<ServerLog>> = database.serverLogDao().getRecentLogs()

    suspend fun initializeDefaultsIfNeeded() {
        val existingDevices = devices.first()
        if (existingDevices.isEmpty()) {
            val defaultDevices = listOf(
                SmartDevice(
                    id = "dev-light-1",
                    name = "Quantum Holo-Pendant",
                    room = "Living Room",
                    type = "LIGHT",
                    isOn = true,
                    value = 85f,
                    colorHex = "#00F5FF",
                    isOnline = true,
                    powerWatts = 24.5f
                ),
                SmartDevice(
                    id = "dev-thermo-1",
                    name = "Neural Climate Core",
                    room = "Central HVAC",
                    type = "THERMOSTAT",
                    isOn = true,
                    value = 21.5f,
                    colorHex = "#00FFCC",
                    isOnline = true,
                    powerWatts = 450f
                ),
                SmartDevice(
                    id = "dev-plug-1",
                    name = "Termux Node Server Rig",
                    room = "Lab Rack A",
                    type = "PLUG",
                    isOn = true,
                    value = 100f,
                    colorHex = "#B026FF",
                    isOnline = true,
                    powerWatts = 65.2f
                ),
                SmartDevice(
                    id = "dev-lock-1",
                    name = "Biometric Vault Gateway",
                    room = "Front Entry",
                    type = "LOCK",
                    isOn = true,
                    value = 1f,
                    colorHex = "#00FF88",
                    isOnline = true,
                    powerWatts = 4.8f
                ),
                SmartDevice(
                    id = "dev-light-2",
                    name = "Cyber Wall Ambient Bars",
                    room = "Lab",
                    type = "LIGHT",
                    isOn = true,
                    value = 70f,
                    colorHex = "#B026FF",
                    isOnline = true,
                    powerWatts = 18.0f
                ),
                SmartDevice(
                    id = "dev-speaker-1",
                    name = "Aether Resonator 360",
                    room = "Studio",
                    type = "SPEAKER",
                    isOn = false,
                    value = 40f,
                    colorHex = "#FFB300",
                    isOnline = true,
                    powerWatts = 12.0f
                )
            )
            database.smartDeviceDao().insertAll(defaultDevices)
        }

        val existingSchedules = schedules.first()
        if (existingSchedules.isEmpty()) {
            val defaultSchedules = listOf(
                ScheduleItem(
                    id = "sch-1",
                    title = "Quantum Morning Awakening",
                    time = "07:00 AM",
                    category = "ROUTINE",
                    days = "Mon, Tue, Wed, Thu, Fri",
                    voiceTrigger = "Good morning Aether",
                    actionPayload = "lights_on:80%,thermostat:22C,audio_briefing",
                    isEnabled = true
                ),
                ScheduleItem(
                    id = "sch-2",
                    title = "Deep Code Focus & Lab Isolation",
                    time = "10:00 AM",
                    category = "AUTOMATION",
                    days = "Everyday",
                    voiceTrigger = "Enter focus matrix",
                    actionPayload = "ambient_violet:40%,mute_speakers,start_termux_daemon",
                    isEnabled = true
                ),
                ScheduleItem(
                    id = "sch-3",
                    title = "Localhost Termux API Heartbeat & Backup",
                    time = "03:00 PM",
                    category = "SYNC",
                    days = "Everyday",
                    voiceTrigger = "Sync nodes now",
                    actionPayload = "ping_devices,sync_database,flush_logs",
                    isEnabled = true
                ),
                ScheduleItem(
                    id = "sch-4",
                    title = "Perimeter Security & Night Slumber",
                    time = "11:30 PM",
                    category = "SECURITY",
                    days = "Everyday",
                    voiceTrigger = "Initiate night lockdown",
                    actionPayload = "lock_all,lights_off,thermostat:19C,termux_low_power",
                    isEnabled = true
                )
            )
            database.scheduleDao().insertAll(defaultSchedules)
        }

        val existingScripts = scripts.first()
        if (existingScripts.isEmpty()) {
            val defaultScripts = listOf(
                VoiceScript(
                    id = "script-1",
                    title = "Termux System Diagnostic & Ping",
                    language = "BASH",
                    triggerPhrase = "run termux diagnostics",
                    code = """#!/bin/bash
echo "[AETHER-LOCAL] Scanning Termux localhost:8080..."
uname -a
echo "Memory: $(cat /proc/meminfo 2>/dev/null | grep MemFree || echo 'Free: 4120 MB')"
echo "Network status: Active interfaces up"
curl -s http://127.0.0.1:8080/api/v1/status || echo '{"status":"ONLINE","port":8080}'
echo "[SUCCESS] Termux host operational."
""",
                    description = "Verifies background server daemon, memory footprint, and network loopback socket.",
                    lastRunStatus = "SUCCESS",
                    lastRunOutput = "[AETHER-LOCAL] Localhost verified. CPU 4.2%, Latency 1ms.",
                    lastRunTime = System.currentTimeMillis() - 3600000
                ),
                VoiceScript(
                    id = "script-2",
                    title = "Smart Home Emergency Lockdown",
                    language = "PYTHON",
                    triggerPhrase = "activate red alert",
                    code = """import requests
import json

payload = {
    "action": "LOCKDOWN",
    "devices": ["dev-lock-1", "dev-light-1", "dev-light-2"],
    "color": "#FF3366",
    "alarm": True
}
print(f"[SEC-GRID] Sending command: {payload['action']}")
# API call to local Termux server
res = requests.post("http://127.0.0.1:8080/api/v1/devices/lockdown", json=payload)
print(f"[STATUS] Vault engaged. Perimeter armed. Response: {res.status_code if 'res' in locals() else '200 OK'}")
""",
                    description = "Locks vault gateways, switches ambient lighting to alert amber/red, and triggers perimeter security.",
                    lastRunStatus = "IDLE",
                    lastRunOutput = "",
                    lastRunTime = 0L
                ),
                VoiceScript(
                    id = "script-3",
                    title = "Daily Schedule Sync & Morning Briefing",
                    language = "JAVASCRIPT",
                    triggerPhrase = "generate morning briefing",
                    code = """async function runMorningSync() {
  const time = new Date().toLocaleTimeString();
  console.log(`[AETHER-SYNC] Executing at ${'$'}time`);
  
  const weather = { temp: "22°C", sky: "Clear", uv: 3 };
  const tasks = ["Termux cluster compile", "Review AI model embeddings", "Smart Home check"];
  
  return {
    speech: `Good morning Commander. All systems optimal. Temperature is ${'$'}{weather.temp}. You have ${'$'}{tasks.length} critical automations queued.`,
    tasksReady: tasks.length
  };
}
runMorningSync().then(res => console.log(JSON.stringify(res, null, 2)));
""",
                    description = "Compiles weather telemetry, daily events, and announces high-priority briefing through TTS.",
                    lastRunStatus = "SUCCESS",
                    lastRunOutput = "[AETHER-SYNC] Briefing synthesized. TTS ready.",
                    lastRunTime = System.currentTimeMillis() - 7200000
                )
            )
            database.voiceScriptDao().insertAll(defaultScripts)
        }
    }

    suspend fun toggleDevicePower(id: String, isOn: Boolean) {
        database.smartDeviceDao().updatePower(id, isOn)
        logServerActivity("/api/v1/devices/$id/power", "POST", 200, "Device $id power set to $isOn")
    }

    suspend fun updateDeviceValue(id: String, value: Float) {
        database.smartDeviceDao().updateValue(id, value)
    }

    suspend fun saveDevice(device: SmartDevice) {
        database.smartDeviceDao().insertOrUpdate(device)
    }

    suspend fun deleteDevice(id: String) {
        database.smartDeviceDao().deleteDevice(id)
    }

    suspend fun toggleSchedule(id: String, isEnabled: Boolean) {
        database.scheduleDao().toggleSchedule(id, isEnabled)
        logServerActivity("/api/v1/schedules/$id/toggle", "PATCH", 200, "Schedule $id enabled: $isEnabled")
    }

    suspend fun saveSchedule(schedule: ScheduleItem) {
        database.scheduleDao().insertOrUpdate(schedule)
        logServerActivity("/api/v1/schedules", "POST", 201, "Created schedule: ${schedule.title}")
    }

    suspend fun deleteSchedule(id: String) {
        database.scheduleDao().deleteSchedule(id)
    }

    suspend fun saveScript(script: VoiceScript) {
        database.voiceScriptDao().insertOrUpdate(script)
    }

    suspend fun deleteScript(id: String) {
        database.voiceScriptDao().deleteScript(id)
    }

    suspend fun updateScriptRunResult(id: String, status: String, output: String) {
        database.voiceScriptDao().updateRunResult(id, status, output, System.currentTimeMillis())
    }

    suspend fun logServerActivity(endpoint: String, method: String, status: Int, details: String) {
        database.serverLogDao().insertLog(
            ServerLog(
                endpoint = endpoint,
                method = method,
                status = status,
                details = details
            )
        )
    }

    suspend fun clearLogs() {
        database.serverLogDao().clearLogs()
    }
}

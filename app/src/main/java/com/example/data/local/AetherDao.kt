package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ScheduleItem
import com.example.data.model.ServerLog
import com.example.data.model.SmartDevice
import com.example.data.model.VoiceScript
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartDeviceDao {
    @Query("SELECT * FROM smart_devices ORDER BY room ASC, name ASC")
    fun getAllDevices(): Flow<List<SmartDevice>>

    @Query("SELECT * FROM smart_devices WHERE id = :id")
    suspend fun getDeviceById(id: String): SmartDevice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(device: SmartDevice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<SmartDevice>)

    @Query("UPDATE smart_devices SET isOn = :isOn WHERE id = :id")
    suspend fun updatePower(id: String, isOn: Boolean)

    @Query("UPDATE smart_devices SET value = :value WHERE id = :id")
    suspend fun updateValue(id: String, value: Float)

    @Query("DELETE FROM smart_devices WHERE id = :id")
    suspend fun deleteDevice(id: String)
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules ORDER BY time ASC")
    fun getAllSchedules(): Flow<List<ScheduleItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(schedule: ScheduleItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<ScheduleItem>)

    @Query("UPDATE schedules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun toggleSchedule(id: String, isEnabled: Boolean)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteSchedule(id: String)
}

@Dao
interface VoiceScriptDao {
    @Query("SELECT * FROM voice_scripts ORDER BY title ASC")
    fun getAllScripts(): Flow<List<VoiceScript>>

    @Query("SELECT * FROM voice_scripts WHERE id = :id")
    suspend fun getScriptById(id: String): VoiceScript?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(script: VoiceScript)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scripts: List<VoiceScript>)

    @Query("UPDATE voice_scripts SET lastRunStatus = :status, lastRunOutput = :output, lastRunTime = :timestamp WHERE id = :id")
    suspend fun updateRunResult(id: String, status: String, output: String, timestamp: Long)

    @Query("DELETE FROM voice_scripts WHERE id = :id")
    suspend fun deleteScript(id: String)
}

@Dao
interface ServerLogDao {
    @Query("SELECT * FROM server_logs ORDER BY timestamp DESC LIMIT 60")
    fun getRecentLogs(): Flow<List<ServerLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ServerLog)

    @Query("DELETE FROM server_logs")
    suspend fun clearLogs()
}

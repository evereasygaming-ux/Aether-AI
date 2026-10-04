package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ScheduleItem
import com.example.data.model.ServerLog
import com.example.data.model.SmartDevice
import com.example.data.model.VoiceScript

@Database(
    entities = [
        SmartDevice::class,
        ScheduleItem::class,
        VoiceScript::class,
        ServerLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun smartDeviceDao(): SmartDeviceDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun voiceScriptDao(): VoiceScriptDao
    abstract fun serverLogDao(): ServerLogDao

    companion object {
        @Volatile
        private var INSTANCE: AetherDatabase? = null

        fun getInstance(context: Context): AetherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AetherDatabase::class.java,
                    "aether_hologram.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

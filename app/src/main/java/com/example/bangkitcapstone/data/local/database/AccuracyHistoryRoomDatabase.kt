package com.example.bangkitcapstone.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AccuracyHistory::class], version = 4, exportSchema = false)
abstract class AccuracyHistoryRoomDatabase : RoomDatabase() {
    abstract fun accuracyHistoryDao(): AccuracyHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AccuracyHistoryRoomDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AccuracyHistoryRoomDatabase {
            if (INSTANCE == null) {
                synchronized(AccuracyHistoryRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AccuracyHistoryRoomDatabase::class.java, "Accuracy History Database"
                    )
                        .build()
                }
            }

            return INSTANCE as AccuracyHistoryRoomDatabase
        }

        @JvmStatic
        fun getDatabase(context: Context): AccuracyHistoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AccuracyHistoryRoomDatabase::class.java, "accuracy_history_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

package com.example.bangkitcapstone.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accuracy_history")
data class AccuracyHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "user_token")
    val userToken: String,

    @ColumnInfo(name = "accuracy")
    val accuracy: Double,

    @ColumnInfo(name = "predicted_aksara")
    val predictedAksara: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
package com.example.bangkitcapstone.data.local.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.paging.DataSource
import androidx.room.Delete
import androidx.room.Query


@Dao
interface AccuracyHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccuracyHistory(accuracyHistory: AccuracyHistory)

    @Query("SELECT * FROM accuracy_history WHERE user_token = :userToken ORDER BY timestamp DESC")
    fun getAllAccuracyHistory(userToken: String): DataSource.Factory<Int, AccuracyHistory>

    @Query("SELECT * FROM accuracy_history WHERE user_token = :userToken ORDER BY accuracy DESC, timestamp DESC")
    fun getAllHighAccuracyHistory(userToken: String): DataSource.Factory<Int, AccuracyHistory>

    @Query("SELECT * FROM accuracy_history WHERE user_token = :userToken ORDER BY accuracy ASC, timestamp DESC")
    fun getAllLowAccuracyHistory(userToken: String): DataSource.Factory<Int, AccuracyHistory>

    @Delete
    suspend fun deleteAccuracyHistory(accuracyHistory: AccuracyHistory)
}
package com.example.bangkitcapstone.data.local.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.paging.DataSource
import androidx.room.Delete


@Dao
interface AccuracyHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccuracyHistory(accuracyHistory: AccuracyHistory)

    @RawQuery(observedEntities = [AccuracyHistory::class])
    fun getAllAccuracyHistory(query: SupportSQLiteQuery): DataSource.Factory<Int, AccuracyHistory>

    @Delete
    suspend fun deleteAccuracyHistory(accuracyHistory: AccuracyHistory)
}
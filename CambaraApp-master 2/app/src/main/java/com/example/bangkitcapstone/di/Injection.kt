package com.example.bangkitcapstone.di

import android.content.Context
import com.example.bangkitcapstone.data.local.database.AccuracyHistoryRoomDatabase
import com.example.bangkitcapstone.data.local.pref.UserPreferences
import com.example.bangkitcapstone.data.local.pref.dataStore
import com.example.bangkitcapstone.data.remote.api.ApiConfig
import com.example.bangkitcapstone.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = AccuracyHistoryRoomDatabase.getDatabase(context)
        return UserRepository.getInstance(pref, apiService, database)
    }
}

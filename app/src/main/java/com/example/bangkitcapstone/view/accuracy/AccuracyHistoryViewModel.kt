package com.example.bangkitcapstone.view.accuracy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.data.repository.UserRepository
import kotlinx.coroutines.launch

class AccuracyHistoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private var highAccuracyHistory: LiveData<PagedList<AccuracyHistory>>? = null
    private var lowAccuracyHistory: LiveData<PagedList<AccuracyHistory>>? = null
    private var accuracyHistory: LiveData<PagedList<AccuracyHistory>>? = null

    fun getAccuracyHistory(userToken: String): LiveData<PagedList<AccuracyHistory>> {
        accuracyHistory = userRepository.getAllAccuracyHistory(userToken)
        return accuracyHistory ?: MutableLiveData()
    }

    fun getAllHighAccuracyHistory(userToken: String): LiveData<PagedList<AccuracyHistory>> {
        highAccuracyHistory = userRepository.getAllHighAccuracyHistory(userToken)
        return highAccuracyHistory ?: MutableLiveData()
    }

    fun getAllLowAccuracyHistory(userToken: String): LiveData<PagedList<AccuracyHistory>> {
        lowAccuracyHistory = userRepository.getAllLowAccuracyHistory(userToken)
        return lowAccuracyHistory ?: MutableLiveData()
    }

    fun deleteAccuracyHistory(accuracyHistory: AccuracyHistory) {
        viewModelScope.launch {
            userRepository.deleteAccuracyHistory(accuracyHistory)
        }
    }
}


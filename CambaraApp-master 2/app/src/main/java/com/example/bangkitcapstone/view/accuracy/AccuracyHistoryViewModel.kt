package com.example.bangkitcapstone.view.accuracy

import com.example.bangkitcapstone.view.utils.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.data.repository.UserRepository
import com.example.bangkitcapstone.view.utils.AccuracySortType
import kotlinx.coroutines.launch


class AccuracyHistoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _sort = MutableLiveData<AccuracySortType>()

    val accuracyHistory: LiveData<PagedList<AccuracyHistory>> = _sort.switchMap {
        userRepository.getAllAccuracyHistory(it)
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _undo = MutableLiveData<Event<AccuracyHistory>>()
    val undo: LiveData<Event<AccuracyHistory>> = _undo

    init {
        _sort.value = AccuracySortType.HIGH_ACCURACY
    }

    fun sort(sortType: AccuracySortType) {
        _sort.value = sortType
    }

    fun deletAccuacyHistory(accuracyHistory: AccuracyHistory){
        viewModelScope.launch {
            userRepository.deleteAccuracyHistory(accuracyHistory)
        }
    }
}

package com.example.bangkitcapstone.view.akasara

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bangkitcapstone.data.remote.response.AksaraItem
import com.example.bangkitcapstone.data.repository.UserRepository
import kotlinx.coroutines.launch
import com.example.bangkitcapstone.data.result.Result

class AksaraViewModel(private val repository: UserRepository): ViewModel() {

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    private val _searchResult = MutableLiveData<Result<List<AksaraItem>>>()
    val searchResult: LiveData<Result<List<AksaraItem>>> get() = _searchResult

    fun getAksara() = repository.getAksara()

    fun searchAksara(newQuery: String){
        _query.value = newQuery
        viewModelScope.launch {
            val result = repository.searchAksara(newQuery)
            _searchResult.value = result
        }
    }
}



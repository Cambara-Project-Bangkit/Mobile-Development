package com.example.bangkitcapstone.view.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.data.local.pref.UserModel
import com.example.bangkitcapstone.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.io.File

class CameraViewModel(private val repository: UserRepository): ViewModel() {
    fun getAksaraDetail(id: String) = repository.getDetailAksara(id)

    fun uploadAksara(aksara: String, image: File) = repository.uploadAksara(aksara,image)

    fun insertAccuracyHistory(accuracyHistory: AccuracyHistory) {
        viewModelScope.launch {
            repository.insertAccuracyHistory(accuracyHistory)
        }
    }

    fun getSession(): LiveData<UserModel>{
        return repository.getSession().asLiveData()
    }
}

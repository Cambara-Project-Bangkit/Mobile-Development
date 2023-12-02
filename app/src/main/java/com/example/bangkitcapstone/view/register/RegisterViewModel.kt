package com.example.bangkitcapstone.view.register

import androidx.lifecycle.ViewModel
import com.example.bangkitcapstone.data.repository.UserRepository

class RegisterViewModel(private val repository: UserRepository):ViewModel() {
    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}
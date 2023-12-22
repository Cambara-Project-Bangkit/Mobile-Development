package com.example.bangkitcapstone

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bangkitcapstone.data.repository.UserRepository
import com.example.bangkitcapstone.di.Injection
import com.example.bangkitcapstone.view.accuracy.AccuracyHistoryViewModel
import com.example.bangkitcapstone.view.akasara.AksaraViewModel
import com.example.bangkitcapstone.view.camera.CameraViewModel
import com.example.bangkitcapstone.view.login.LoginViewModel
import com.example.bangkitcapstone.view.main.MainViewModel
import com.example.bangkitcapstone.view.signup.SignupViewModel

class ViewModelFactory(private val userRepository: UserRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(AksaraViewModel::class.java)->{
                AksaraViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java)->{
                CameraViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(AccuracyHistoryViewModel::class.java)->{
                AccuracyHistoryViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context,): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context),
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}

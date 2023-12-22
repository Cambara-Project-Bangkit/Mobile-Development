package com.example.bangkitcapstone.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.bangkitcapstone.data.local.database.AccuracyHistory
import com.example.bangkitcapstone.data.local.database.AccuracyHistoryDao
import com.example.bangkitcapstone.data.local.database.AccuracyHistoryRoomDatabase
import com.example.bangkitcapstone.data.remote.api.ApiService
import com.example.bangkitcapstone.data.remote.response.RegisterResponse
import com.example.bangkitcapstone.data.local.pref.UserModel
import com.example.bangkitcapstone.data.local.pref.UserPreferences
import com.example.bangkitcapstone.data.remote.api.ApiConfig
import com.example.bangkitcapstone.data.remote.response.AksaraItem
import com.example.bangkitcapstone.data.remote.response.AksaraResponse
import com.example.bangkitcapstone.data.remote.response.DetailAksaraResponse
import com.example.bangkitcapstone.data.remote.response.ErrorResponse
import com.example.bangkitcapstone.data.remote.response.LoginResponse
import com.example.bangkitcapstone.data.remote.response.UploadResponse
import com.google.gson.Gson
import retrofit2.HttpException
import com.example.bangkitcapstone.data.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class UserRepository private  constructor(
    private val userPreferences: UserPreferences,
    private val apiService: ApiService,
    private val accuracyHistoryRoomDatabase: AccuracyHistoryRoomDatabase
) {
    private val accuracyHistoryDao: AccuracyHistoryDao = accuracyHistoryRoomDatabase.accuracyHistoryDao()


    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }

    suspend fun logout() {
        userPreferences.logout()
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, email, password)
            if (registerResponse.error == false) {
                emit(Result.Success(registerResponse))
            } else {
                emit(Result.Error(registerResponse.message ?: "Error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error("Registration Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            if (!loginResponse.error) {
                val user = UserModel(
                    email = email,
                    token = loginResponse.user.token,
                    isLogin = true
                )
                ApiConfig.token = loginResponse.message
                userPreferences.saveSession(user)
                emit(Result.Success(loginResponse))
            } else {
                emit(Result.Error(loginResponse.message))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error("Login Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }

    fun parseAksaraJson(jsonString: String): List<AksaraItem> {
        val gson = Gson()
        val aksaraResponse: AksaraResponse = gson.fromJson(jsonString, AksaraResponse::class.java)
        return aksaraResponse.aksara
    }

    fun getAksara(): LiveData<Result<List<AksaraItem>>> = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val aksaraRespone = response.getAksara()
            Log.d("AksaraResponse", "JSON Response: ${Gson().toJson(aksaraRespone)}")

            val aksaraList = parseAksaraJson(Gson().toJson(aksaraRespone))

            if (aksaraRespone.error == false) {
                emit(Result.Success(aksaraList))
            } else {
                emit(Result.Error(aksaraRespone.message))
            }
        } catch (error: HttpException) {

        } catch (e: Exception) {

        }
    }

    suspend fun searchAksara(query: String): Result<List<AksaraItem>> {
        return try {
            val user = userPreferences.getSession().first()
            val response = ApiConfig.getApiService(user.token)
            val aksaraResponse = response.getAksara()

            if (aksaraResponse.error == false) {
                val aksaraList = parseAksaraJson(Gson().toJson(aksaraResponse))


                val filteredList = if (query.isNotBlank()) {
                    aksaraList.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                } else {
                    aksaraList
                }

                Result.Success(filteredList)
            } else {
                Result.Error(aksaraResponse.message)
            }
        } catch (error: HttpException) {
            Result.Error("Network error")
        } catch (e: Exception) {
            Result.Error("Unexpected error")
        }
    }

    fun getDetailAksara(id: String): LiveData<Result<DetailAksaraResponse>> = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { userPreferences.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val detailAksaraResponse = response.getDetailAksara(id)
            if (detailAksaraResponse != null) {
                emit(Result.Success(detailAksaraResponse))
            } else {
                emit(Result.Error("error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            Log.d("Detail: ", errorMessage.toString())
            emit(Result.Error("Loading Failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }

     fun uploadAksara(aksara: String, imageFile: File): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)
         val requestBody = aksara.toRequestBody("text/plain".toMediaType())
         val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
         val multipartBody = MultipartBody.Part.createFormData(
             "image",
             imageFile.name,
             requestImageFile
         )
         try {
             val user = runBlocking { userPreferences.getSession().first() }
             val response = ApiConfig.getApiService(user.token)
             val successResponse = response.uploadImage(requestBody, multipartBody)
             emit(Result.Success(successResponse))
         } catch (e: HttpException) {
             val errorBody = e.response()?.errorBody()?.string()
             val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
             emit(Result.Error(errorResponse.message))
         }
         
         
    }

    fun getAllAccuracyHistory(userToken: String): LiveData<PagedList<AccuracyHistory>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20)
            .build()

        val dataSourceFactory = accuracyHistoryDao.getAllAccuracyHistory(userToken)
        return LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun getAllHighAccuracyHistory(userToken: String): LiveData<PagedList<AccuracyHistory>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20)
            .build()

        val dataSourceFactory = accuracyHistoryDao.getAllHighAccuracyHistory(userToken)
        return LivePagedListBuilder(dataSourceFactory, config).build()
    }

    fun getAllLowAccuracyHistory(userToken: String): LiveData<PagedList<AccuracyHistory>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20)
            .build()

        val dataSourceFactory = accuracyHistoryDao.getAllLowAccuracyHistory(userToken)
        return LivePagedListBuilder(dataSourceFactory, config).build()
    }


    suspend fun deleteAccuracyHistory(accuracyHistory: AccuracyHistory){
        accuracyHistoryDao.deleteAccuracyHistory(accuracyHistory)
    }

    suspend fun insertAccuracyHistory(accuracyHistory: AccuracyHistory) {
        accuracyHistoryDao.insertAccuracyHistory(accuracyHistory)

    }
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreferences: UserPreferences,
            apiService: ApiService,
            accuracyHistoryRoomDatabase: AccuracyHistoryRoomDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreferences,apiService, accuracyHistoryRoomDatabase)
            }.also { instance = it }
    }
}
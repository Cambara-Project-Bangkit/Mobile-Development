package com.example.bangkitcapstone.data.remote.api

import com.example.bangkitcapstone.data.remote.response.AksaraResponse
import com.example.bangkitcapstone.data.remote.response.LoginResponse
import com.example.bangkitcapstone.data.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) :RegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("aksara")
    suspend fun getAksara():AksaraResponse
}


package com.example.bangkitcapstone.data.remote.api

import com.example.bangkitcapstone.data.remote.response.AksaraResponse
import com.example.bangkitcapstone.data.remote.response.DetailAksaraResponse
import com.example.bangkitcapstone.data.remote.response.LoginResponse
import com.example.bangkitcapstone.data.remote.response.RegisterResponse
import com.example.bangkitcapstone.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

    @GET("aksara/{id}")
    suspend fun getDetailAksara(
        @Path("id") id: String
    ): DetailAksaraResponse


    @Multipart
    @POST("upload")
    suspend fun uploadImage(
        @Part("aksara") aksara: RequestBody,
        @Part file: MultipartBody.Part,
    ) : UploadResponse
}


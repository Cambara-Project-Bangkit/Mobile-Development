package com.example.bangkitcapstone.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: User
)

data class User(

	@field:SerializedName("UserId")
	val userId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("token")
	val token: String
)

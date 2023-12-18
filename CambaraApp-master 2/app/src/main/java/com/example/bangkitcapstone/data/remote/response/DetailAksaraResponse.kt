package com.example.bangkitcapstone.data.remote.response

import com.google.gson.annotations.SerializedName

data class DetailAksaraResponse(

	@field:SerializedName("aksara")
	val aksara: Aksara,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Aksara(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("urlImage")
	val urlImage: String
)

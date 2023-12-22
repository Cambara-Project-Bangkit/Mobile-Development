package com.example.bangkitcapstone.data.remote.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class Data(

	@field:SerializedName("accuracy")
	val accuracy: String,

	@field:SerializedName("predicted_aksara")
	val predicted_aksara: String
)

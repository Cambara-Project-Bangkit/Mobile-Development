package com.example.bangkitcapstone.data.remote.response

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class AksaraResponse(
	@SerializedName("aksara")
	val aksara: List<AksaraItem>,
	@SerializedName("error")
	val error: Boolean,
	@SerializedName("message")
	val message: String
)

data class AksaraItem(
	@SerializedName("id")
	val id: String,
	@SerializedName("name")
	val name: String,
	@SerializedName("description")
	val description: String,
	@SerializedName("urlImage")
	val urlImage: String,
	@SerializedName("urlYoutube")
	val urlYoutube: String
)


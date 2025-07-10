package com.nesto.butcharytokens.model

import com.google.gson.annotations.SerializedName

data class NewTokenResponse(

	@field:SerializedName("customer_url")
	val customerUrl: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("token_number")
	val tokenNumber: String? = null,

	@field:SerializedName("contact_number")
	val contactNumber: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

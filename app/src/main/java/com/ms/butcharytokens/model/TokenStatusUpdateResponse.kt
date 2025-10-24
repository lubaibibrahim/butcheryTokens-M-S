package com.ms.butcharytokens.model

import com.google.gson.annotations.SerializedName

data class TokenStatusUpdateResponse(

	@field:SerializedName("new_status")
	val newStatus: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

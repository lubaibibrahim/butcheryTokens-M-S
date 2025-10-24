package com.ms.butcharytokens.model

import com.google.gson.annotations.SerializedName

data class NewTokenRequest(

    @field:SerializedName("token_number")
    var token_number: String? = null,

    @field:SerializedName("contact_number")
    var contact_number: String? = null,

    @field:SerializedName("dept")
    var dept: String? = null,

    @field:SerializedName("store")
    var store: String? = null
)

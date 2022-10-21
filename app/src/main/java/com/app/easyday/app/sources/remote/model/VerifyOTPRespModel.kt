package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class VerifyOTPRespModel(

	@field:SerializedName("is_new_user")
	val isNewUser: Boolean? = null,

	@field:SerializedName("token")
	val token: String? = null
)

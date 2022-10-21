package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class OTPRespModel(

	@field:SerializedName("otp")
	val otp: Int? = null
)

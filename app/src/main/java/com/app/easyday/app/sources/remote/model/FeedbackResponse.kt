package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class FeedbackResponse(
	val data: Data? = null,
	val success: Boolean? = null,
	val message: String? = null
)

data class Data(
	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("rating")
	val rating: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("feedback_text")
	val feedbackText: String? = null,

	@field:SerializedName("tags")
	val tags: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)


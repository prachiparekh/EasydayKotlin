package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName


data class FeedbackResponse(
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
) {
	override fun toString(): String {
		return "FeedbackResponse(createdAt=$createdAt, userId=$userId, rating=$rating, id=$id, feedbackText=$feedbackText, tags=$tags, updatedAt=$updatedAt)"
	}
}


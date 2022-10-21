package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class AttributeResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("attribute_name")
	val attributeName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

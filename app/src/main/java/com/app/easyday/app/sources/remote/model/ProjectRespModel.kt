package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class ProjectRespModel(
	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("assign_color")
	val assignColor: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("project_name")
	val projectName: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("project_participants")
	val projectParticipants: List<ProjectParticipantsModel?>? = null,

	@field:SerializedName("project_invited")
	val projectInvited: List<ProjectInvitedItem?>? = null,
)

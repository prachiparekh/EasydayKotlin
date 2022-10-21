package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class TaskResponse(

	@field:SerializedName("due_date")
	val dueDate: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("task_media")
	val taskMedia: List<TaskMediaItem?>? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("priority")
	val priority: Int? = null,

	@field:SerializedName("red_flag")
	val redFlag: Boolean? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("task_spaces")
	val taskSpaces: List<TaskAttributeResponse?>? = null,

	@field:SerializedName("task_zones")
	val taskZones: List<TaskAttributeResponse?>? = null,

	@field:SerializedName("task_comments")
	val taskComments: List<Any?>? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("project_participant_id")
	val projectParticipantId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("task_tags")
	val taskTags: List<TaskAttributeResponse?>? = null,

	@field:SerializedName("task_participants")
	val taskParticipants: List<TaskParticipantsItem?>? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)





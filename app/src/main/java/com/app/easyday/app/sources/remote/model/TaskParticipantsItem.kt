package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class TaskParticipantsItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("project_participant_id")
    val projectParticipantId: Int? = null,

    @field:SerializedName("task_id")
    val taskId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: UserModel? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)
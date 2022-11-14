package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class ReportResponse(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("tasksData")
    val tasksData: ReportTasksData? = null,

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

    @field:SerializedName("project_participants")
    val projectParticipants: List<ReportParticipantsModel?>? = null,

    @field:SerializedName("project_tags")
    val projectTags: List<ReportTagResponse?>? = null
) {
    override fun toString(): String {
        return "ReportResponse(createdAt=$createdAt, tasksData=$tasksData, userId=$userId, assignColor=$assignColor, description=$description, id=$id, projectName=$projectName, projectParticipants=$projectParticipants, projectTags=$projectTags)"
    }
}





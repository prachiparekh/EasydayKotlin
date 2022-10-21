package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class AddTaskRequestModel(

    @field:SerializedName("project_id")
    val project_id: Int? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("priority")
    val priority: Int? = null,

    @field:SerializedName("red_flag")
    val red_flag: Int? = null,

    @field:SerializedName("due_date")
    val due_date: String? = null,

    @field:SerializedName("tags")
    var tags: ArrayList<Int>? = null,

    @field:SerializedName("zones")
    var zones: ArrayList<Int>? = null,

    @field:SerializedName("spaces")
    var spaces: ArrayList<Int>? = null,

    @field:SerializedName("task_media")
    var task_media: List<MultipartBody.Part>? = null,

    @field:SerializedName("task_participants")
    var task_participants: ArrayList<Int>? = null
) {
    override fun toString(): String {
        return "AddTaskRequestModel(project_id=$project_id, title=$title, description=$description, priority=$priority, red_flag=$red_flag, due_date=$due_date, tags=$tags, zones=$zones, spaces=$spaces, task_media=$task_media, task_participants=$task_participants)"
    }
}

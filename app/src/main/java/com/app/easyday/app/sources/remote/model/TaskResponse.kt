package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
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
    val taskComments: List<CommentResponseItem?>? = null,

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(TaskMediaItem),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.createTypedArrayList(TaskAttributeResponse),
        parcel.createTypedArrayList(TaskAttributeResponse),
        parcel.createTypedArrayList(CommentResponseItem),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(TaskAttributeResponse),
        parcel.createTypedArrayList(TaskParticipantsItem),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dueDate)
        parcel.writeString(description)
        parcel.writeTypedList(taskMedia)
        parcel.writeString(title)
        parcel.writeValue(priority)
        parcel.writeValue(redFlag)
        parcel.writeString(createdAt)
        parcel.writeTypedList(taskSpaces)
        parcel.writeTypedList(taskZones)
        parcel.writeTypedList(taskComments)
        parcel.writeValue(projectId)
        parcel.writeValue(userId)
        parcel.writeValue(projectParticipantId)
        parcel.writeValue(id)
        parcel.writeTypedList(taskTags)
        parcel.writeTypedList(taskParticipants)
        parcel.writeValue(status)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskResponse> {
        override fun createFromParcel(parcel: Parcel): TaskResponse {
            return TaskResponse(parcel)
        }

        override fun newArray(size: Int): Array<TaskResponse?> {
            return arrayOfNulls(size)
        }
    }
}
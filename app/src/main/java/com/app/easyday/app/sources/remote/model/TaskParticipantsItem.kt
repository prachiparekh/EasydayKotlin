package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
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
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(UserModel::class.java.classLoader),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeValue(userId)
        parcel.writeValue(projectParticipantId)
        parcel.writeValue(taskId)
        parcel.writeValue(id)
        parcel.writeParcelable(user, flags)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskParticipantsItem> {
        override fun createFromParcel(parcel: Parcel): TaskParticipantsItem {
            return TaskParticipantsItem(parcel)
        }

        override fun newArray(size: Int): Array<TaskParticipantsItem?> {
            return arrayOfNulls(size)
        }
    }
}
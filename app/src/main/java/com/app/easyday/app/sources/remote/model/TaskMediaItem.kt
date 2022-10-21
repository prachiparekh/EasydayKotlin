package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TaskMediaItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("task_id")
    val taskId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("type")
    val type: Int? = null,

    @field:SerializedName("media_url")
    val mediaUrl: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeValue(taskId)
        parcel.writeValue(id)
        parcel.writeValue(type)
        parcel.writeString(mediaUrl)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskMediaItem> {
        override fun createFromParcel(parcel: Parcel): TaskMediaItem {
            return TaskMediaItem(parcel)
        }

        override fun newArray(size: Int): Array<TaskMediaItem?> {
            return arrayOfNulls(size)
        }
    }
}
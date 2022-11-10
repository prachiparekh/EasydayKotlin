package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class TaskCommentMedia(

    @field:SerializedName("media_url")
    val media_url: String? = null,

    @field:SerializedName("type")
    val type: Int? = null,

    @field:SerializedName("duration")
    val duration: Int? = null,


    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(media_url)
        parcel.writeValue(type)
        parcel.writeValue(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskCommentMedia> {
        override fun createFromParcel(parcel: Parcel): TaskCommentMedia {
            return TaskCommentMedia(parcel)
        }

        override fun newArray(size: Int): Array<TaskCommentMedia?> {
            return arrayOfNulls(size)
        }
    }
}

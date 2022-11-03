package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class TaskAttributeResponse(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("project_attribute_id")
    val projectAttributeId: Int? = null,

    @field:SerializedName("task_id")
    val taskId: Int? = null,

    @field:SerializedName("project_attribute")
    val projectAttribute: AttributeResponse? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(AttributeResponse::class.java.classLoader),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeValue(projectAttributeId)
        parcel.writeValue(taskId)
        parcel.writeParcelable(projectAttribute, flags)
        parcel.writeValue(id)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskAttributeResponse> {
        override fun createFromParcel(parcel: Parcel): TaskAttributeResponse {
            return TaskAttributeResponse(parcel)
        }

        override fun newArray(size: Int): Array<TaskAttributeResponse?> {
            return arrayOfNulls(size)
        }
    }
}

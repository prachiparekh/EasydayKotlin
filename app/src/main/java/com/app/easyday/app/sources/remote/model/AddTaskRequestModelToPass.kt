package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AddTaskRequestModelToPass(

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
    var zones: Int? = null,

    @field:SerializedName("spaces")
    var spaces: Int? = null,

    @field:SerializedName("task_media")
    var task_media: ArrayList<String>? = null,

    @field:SerializedName("task_participants")
    var task_participants: ArrayList<Int>? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        TODO("tags"),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        TODO("task_media"),
        TODO("task_participants")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(project_id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeValue(priority)
        parcel.writeValue(red_flag)
        parcel.writeString(due_date)
        parcel.writeValue(zones)
        parcel.writeValue(spaces)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddTaskRequestModelToPass> {
        override fun createFromParcel(parcel: Parcel): AddTaskRequestModelToPass {
            return AddTaskRequestModelToPass(parcel)
        }

        override fun newArray(size: Int): Array<AddTaskRequestModelToPass?> {
            return arrayOfNulls(size)
        }
    }


}

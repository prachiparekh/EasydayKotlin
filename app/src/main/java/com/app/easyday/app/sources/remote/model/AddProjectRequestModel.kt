package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.app.easyday.app.sources.local.model.ContactModel
import com.google.gson.annotations.SerializedName

data class AddProjectRequestModel(

    @field:SerializedName("assign_color")
    val assignColor: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("project_name")
    val projectName: String? = null,

    @field:SerializedName("participants")
    var participants: ArrayList<ContactModel>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

        )

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(assignColor)
        p0?.writeString(description)
        p0?.writeString(projectName)
    }

    override fun toString(): String {
        return "AddProjectRequestModel(assignColor=$assignColor, description=$description, projectName=$projectName, participants=$participants)"
    }

    companion object CREATOR : Parcelable.Creator<AddProjectRequestModel> {
        override fun createFromParcel(parcel: Parcel): AddProjectRequestModel {
            return AddProjectRequestModel(parcel)
        }

        override fun newArray(size: Int): Array<AddProjectRequestModel?> {
            return arrayOfNulls(size)
        }
    }
}


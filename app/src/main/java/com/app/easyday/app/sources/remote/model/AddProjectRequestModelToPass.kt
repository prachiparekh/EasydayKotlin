package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.app.easyday.app.sources.local.model.ContactModel
import com.google.gson.annotations.SerializedName

data class AddProjectRequestModelToPass (
    @field:SerializedName("assign_color")
    val assignColor: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("project_name")
    val projectName: String? = null,

    @field:SerializedName("participants")
    var participants: ArrayList<ContactModelToPass>? = null
        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(assignColor)
        parcel.writeString(description)
        parcel.writeString(projectName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddProjectRequestModelToPass> {
        override fun createFromParcel(parcel: Parcel): AddProjectRequestModelToPass {
            return AddProjectRequestModelToPass(parcel)
        }

        override fun newArray(size: Int): Array<AddProjectRequestModelToPass?> {
            return arrayOfNulls(size)
        }
    }
}
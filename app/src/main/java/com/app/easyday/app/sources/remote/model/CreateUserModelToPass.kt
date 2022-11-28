package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CreateUserModelToPass(

    @field:SerializedName("fullname")
    val fullname: String? = null,
    @field:SerializedName("profession")
    val profession: String? = null,
    @field:SerializedName("profile_image")
    val profile_image: String? = null,
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullname)
        parcel.writeString(profession)
        parcel.writeString(profile_image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CreateUserModelToPass> {
        override fun createFromParcel(parcel: Parcel): CreateUserModelToPass {
            return CreateUserModelToPass(parcel)
        }

        override fun newArray(size: Int): Array<CreateUserModelToPass?> {
            return arrayOfNulls(size)
        }
    }
}
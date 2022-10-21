package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ContactModelToPass (
    @field:SerializedName("role")
    var role: String? = null,

    @field:SerializedName("phone_number")
    var phone_number: String? = null,

    @field:SerializedName("country_code")
    var country_code: String? = null
        ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(role)
        parcel.writeString(phone_number)
        parcel.writeString(country_code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactModelToPass> {
        override fun createFromParcel(parcel: Parcel): ContactModelToPass {
            return ContactModelToPass(parcel)
        }

        override fun newArray(size: Int): Array<ContactModelToPass?> {
            return arrayOfNulls(size)
        }
    }
}

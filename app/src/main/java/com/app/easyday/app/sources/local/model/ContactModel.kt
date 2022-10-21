package com.app.easyday.app.sources.local.model

import android.os.Parcel
import android.os.Parcelable


class ContactModel (
    var id: String? = null,
    var name: String? = null,
    var role: String? = null,
    var phoneNumber: String? = null,
    var countryCode: String? = null,
    var photoURI: String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(role)
        parcel.writeString(phoneNumber)
        parcel.writeString(countryCode)
        parcel.writeString(photoURI)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactModel> {
        override fun createFromParcel(parcel: Parcel): ContactModel {
            return ContactModel(parcel)
        }

        override fun newArray(size: Int): Array<ContactModel?> {
            return arrayOfNulls(size)
        }
    }
}
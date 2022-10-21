package com.app.easyday.app.sources.local.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Media(
    var uri: Uri?,
    val isVideo: Boolean,
    val date: Long,
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeByte(if (isVideo) 1 else 0)
        parcel.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Media(uri=$uri, isVideo=$isVideo, date=$date)"
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }
    }

}

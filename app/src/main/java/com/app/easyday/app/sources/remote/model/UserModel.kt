package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserModel(

	@field:SerializedName("profession")
	val profession: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("profile_image")
	val profileImage: String? = null,

	@field:SerializedName("is_invisible")
	val isInvisible: Boolean? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: Long? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("fullname")
	val fullname: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("country_code")
	val countryCode: String? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
		parcel.readValue(Long::class.java.classLoader) as? Long,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(profession)
		parcel.writeString(createdAt)
		parcel.writeString(profileImage)
		parcel.writeValue(isInvisible)
		parcel.writeValue(phoneNumber)
		parcel.writeValue(id)
		parcel.writeString(fullname)
		parcel.writeString(updatedAt)
		parcel.writeString(countryCode)
	}

	override fun describeContents(): Int {
		return 0
	}

	override fun toString(): String {
		return "UserModel(profession=$profession, phoneNumber=$phoneNumber, fullname=$fullname)"
	}

	companion object CREATOR : Parcelable.Creator<UserModel> {
		override fun createFromParcel(parcel: Parcel): UserModel {
			return UserModel(parcel)
		}

		override fun newArray(size: Int): Array<UserModel?> {
			return arrayOfNulls(size)
		}
	}
}
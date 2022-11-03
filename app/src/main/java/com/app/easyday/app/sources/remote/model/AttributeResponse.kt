package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class AttributeResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("project_id")
	val projectId: Int? = null,

	@field:SerializedName("attribute_name")
	val attributeName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(createdAt)
		parcel.writeValue(projectId)
		parcel.writeString(attributeName)
		parcel.writeValue(id)
		parcel.writeValue(type)
		parcel.writeValue(status)
		parcel.writeString(updatedAt)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<AttributeResponse> {
		override fun createFromParcel(parcel: Parcel): AttributeResponse {
			return AttributeResponse(parcel)
		}

		override fun newArray(size: Int): Array<AttributeResponse?> {
			return arrayOfNulls(size)
		}
	}
}

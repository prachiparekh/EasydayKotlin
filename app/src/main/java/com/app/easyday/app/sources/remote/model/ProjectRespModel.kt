package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ProjectRespModel(
	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("assign_color")
	val assignColor: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("project_name")
	val projectName: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("project_participants")
	var projectParticipants: List<ProjectParticipantsModel?>? = null,

	@field:SerializedName("project_invited")
	val projectInvited: List<ProjectInvitedItem?>? = null,
) : Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readInt(),
		parcel.readString(),
		parcel.readString(),
		parcel.readInt(),
		parcel.readString(),
		parcel.readInt(),
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
		return "AddProjectRequestModel(assignColor=$assignColor, description=$description, projectName=$projectName, participants=$projectParticipants)"
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

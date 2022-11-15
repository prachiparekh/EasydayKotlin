package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class TaskCommentLikeResponse(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("task_comment_id")
	val taskCommentId: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(createdAt)
		parcel.writeValue(taskCommentId)
		parcel.writeValue(userId)
		parcel.writeValue(id)
		parcel.writeString(updatedAt)
	}

	override fun describeContents(): Int {
		return 0
	}

	override fun toString(): String {
		return "TaskCommentLikeResponse(createdAt=$createdAt, taskCommentId=$taskCommentId, userId=$userId, id=$id, updatedAt=$updatedAt)"
	}

	companion object CREATOR : Parcelable.Creator<TaskCommentLikeResponse> {
		override fun createFromParcel(parcel: Parcel): TaskCommentLikeResponse {
			return TaskCommentLikeResponse(parcel)
		}

		override fun newArray(size: Int): Array<TaskCommentLikeResponse?> {
			return arrayOfNulls(size)
		}
	}
}

package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CommentResponseItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("task_comment_media")
	val taskCommentMedia: List<TaskMediaItem?>? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("children")
	val children: List<ChildrenItem?>? = null,

	@field:SerializedName("parent_id")
	val parentId: Int? = null,

	@field:SerializedName("task_id")
	val taskId: Int? = null,

	@field:SerializedName("comment")
	val comment: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("media_url")
	val mediaUrl: String? = null,

	@field:SerializedName("user")
	val user: UserModel? = null,

	@field:SerializedName("task_comment_likes")
	val taskCommentLikes: List<Any?>? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.createTypedArrayList(TaskMediaItem),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(ChildrenItem),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readParcelable(UserModel::class.java.classLoader),
        TODO("taskCommentLikes"),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeTypedList(taskCommentMedia)
        parcel.writeValue(userId)
        parcel.writeTypedList(children)
        parcel.writeValue(parentId)
        parcel.writeValue(taskId)
        parcel.writeString(comment)
        parcel.writeValue(id)
        parcel.writeString(mediaUrl)
        parcel.writeParcelable(user, flags)
        parcel.writeString(updatedAt)
    }

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<CommentResponseItem> {
		override fun createFromParcel(parcel: Parcel): CommentResponseItem {
			return CommentResponseItem(parcel)
		}

		override fun newArray(size: Int): Array<CommentResponseItem?> {
			return arrayOfNulls(size)
		}
	}
}

data class ChildrenItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("children")
    val children: List<ChildrenItem?>? = null,

    @field:SerializedName("parent_id")
    val parentId: Int? = null,

    @field:SerializedName("task_id")
    val taskId: Int? = null,

    @field:SerializedName("comment")
    val comment: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user")
    val user: UserModel? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("task_comment_media")
    val taskCommentMedia: List<TaskCommentMedia?>? = null,

    @field:SerializedName("media_url")
    val mediaUrl: String? = null,

    @field:SerializedName("task_comment_likes")
    val taskCommentLikes: List<Any?>? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.createTypedArrayList(CREATOR),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readParcelable(UserModel::class.java.classLoader),
        parcel.readString(),
        parcel.createTypedArrayList(TaskCommentMedia),
        parcel.readString(),
        TODO("taskCommentLikes")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdAt)
        parcel.writeValue(userId)
        parcel.writeTypedList(children)
        parcel.writeValue(parentId)
        parcel.writeValue(taskId)
        parcel.writeString(comment)
        parcel.writeValue(id)
        parcel.writeParcelable(user, flags)
        parcel.writeString(updatedAt)
        parcel.writeTypedList(taskCommentMedia)
        parcel.writeString(mediaUrl)
    }

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ChildrenItem> {
		override fun createFromParcel(parcel: Parcel): ChildrenItem {
			return ChildrenItem(parcel)
		}

		override fun newArray(size: Int): Array<ChildrenItem?> {
			return arrayOfNulls(size)
		}
	}
}
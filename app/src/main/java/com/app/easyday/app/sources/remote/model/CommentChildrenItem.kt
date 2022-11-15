package com.app.easyday.app.sources.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CommentChildrenItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("user_id")
    val userId: Int? = null,

    @field:SerializedName("children")
    val children: List<CommentChildrenItem?>? = null,

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
    val taskCommentLikes: List<TaskCommentLikeResponse?>? = null
) : Parcelable {
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
        parcel.createTypedArrayList(TaskCommentLikeResponse)
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
        parcel.writeTypedList(taskCommentLikes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommentChildrenItem> {
        override fun createFromParcel(parcel: Parcel): CommentChildrenItem {
            return CommentChildrenItem(parcel)
        }

        override fun newArray(size: Int): Array<CommentChildrenItem?> {
            return arrayOfNulls(size)
        }
    }
}
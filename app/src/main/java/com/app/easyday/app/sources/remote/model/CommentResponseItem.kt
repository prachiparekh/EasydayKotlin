package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class CommentResponseItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("task_comment_media")
	val taskCommentMedia: List<Any?>? = null,

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
	val mediaUrl: Any? = null,

	@field:SerializedName("user")
	val user: UserModel? = null,

	@field:SerializedName("task_comment_likes")
	val taskCommentLikes: List<Any?>? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class ChildrenItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("children")
	val children: List<Any?>? = null,

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
	val taskCommentMedia: List<Any?>? = null,

	@field:SerializedName("media_url")
	val mediaUrl: Any? = null,

	@field:SerializedName("task_comment_likes")
	val taskCommentLikes: List<Any?>? = null
)

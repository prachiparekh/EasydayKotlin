package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class CommentMediaRequest(
    @field:SerializedName("task_id")
    val task_id: Int? = null,

    @field:SerializedName("parent_id")
    val parent_id: Int? = null,

    @field:SerializedName("task_comment_media")
    val task_comment_media: ArrayList<TaskCommentMedia>? = null,
)
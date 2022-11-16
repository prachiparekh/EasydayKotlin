package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class LikeCommentResponse(

    @field:SerializedName("like_counts")
    val likeCounts: Int? = null,

    @field:SerializedName("is_like")
    val isLike: Boolean? = null,

    @field:SerializedName("comment_id")
    val comment_id: Int? = null
) {
    override fun toString(): String {
        return "LikeCommentResponse(likeCounts=$likeCounts, isLike=$isLike, comment_id=$comment_id)"
    }

}

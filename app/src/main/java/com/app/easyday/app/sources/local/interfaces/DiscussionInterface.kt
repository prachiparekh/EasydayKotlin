package com.app.easyday.app.sources.local.interfaces

import com.app.easyday.app.sources.remote.model.CommentResponseItem

interface DiscussionInterface {
    fun onLikeClick(commentID: Int)
    fun onReplyClick(parentComment: CommentResponseItem)
}
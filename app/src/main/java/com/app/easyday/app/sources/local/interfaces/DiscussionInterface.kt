package com.app.easyday.app.sources.local.interfaces

interface DiscussionInterface {
    fun onLikeClick(commentID: Int)
    fun onReplyClick(parentCommentID: Int)
}
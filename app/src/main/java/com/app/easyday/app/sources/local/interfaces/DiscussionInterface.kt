package com.app.easyday.app.sources.local.interfaces

import android.widget.ProgressBar
import android.widget.TextView
import com.app.easyday.app.sources.remote.model.TaskMediaItem

interface DiscussionInterface {
    fun onLikeClick(commentID: Int)
    fun onReplyClick(parentCommentID: Int)
    fun onAudioBtnClick(mediaModel: TaskMediaItem, progressBar: ProgressBar, durationTV: TextView)
}
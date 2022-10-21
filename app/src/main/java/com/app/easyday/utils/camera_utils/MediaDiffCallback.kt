package com.app.easyday.utils.camera_utils

import androidx.recyclerview.widget.DiffUtil
import com.app.easyday.app.sources.local.model.Media
import com.app.easyday.app.sources.remote.model.TaskMediaItem

class MediaDiffCallback : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean = oldItem.uri == newItem.uri

    override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean = oldItem == newItem
}

class TaskMediaDiffCallback : DiffUtil.ItemCallback<TaskMediaItem>() {
    override fun areItemsTheSame(oldItem: TaskMediaItem, newItem: TaskMediaItem): Boolean = oldItem.mediaUrl == newItem.mediaUrl

    override fun areContentsTheSame(oldItem: TaskMediaItem, newItem: TaskMediaItem): Boolean = oldItem == newItem
}


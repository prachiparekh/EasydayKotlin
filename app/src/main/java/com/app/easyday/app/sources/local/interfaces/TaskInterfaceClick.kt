package com.app.easyday.app.sources.local.interfaces

import com.app.easyday.app.sources.remote.model.TaskResponse

interface TaskInterfaceClick {
    fun onTaskClick(taskModel: TaskResponse)
    fun onDiscussionClick(taskModel: TaskResponse)
    fun onSearchResult(count: Int)
}
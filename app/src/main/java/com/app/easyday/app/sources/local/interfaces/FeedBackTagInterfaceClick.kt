package com.app.easyday.app.sources.local.interfaces

import android.widget.RelativeLayout
import com.app.easyday.app.sources.remote.model.TaskResponse
import java.text.FieldPosition

interface FeedBackTagInterfaceClick {
    fun onTagClick(position: Int, title_bg: RelativeLayout, )
}
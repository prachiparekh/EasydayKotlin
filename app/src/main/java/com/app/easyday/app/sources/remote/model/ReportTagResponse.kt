package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class ReportTagResponse(

    @field:SerializedName("tag_name")
    val tagName: String? = null,

    @field:SerializedName("project_tags_count")
    val projectTagsCount: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null
)


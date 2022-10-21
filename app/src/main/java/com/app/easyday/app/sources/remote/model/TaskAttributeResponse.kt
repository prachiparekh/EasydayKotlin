package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName


data class TaskAttributeResponse(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("project_attribute_id")
    val projectAttributeId: Int? = null,

    @field:SerializedName("task_id")
    val taskId: Int? = null,

    @field:SerializedName("project_attribute")
    val projectAttribute: AttributeResponse? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)


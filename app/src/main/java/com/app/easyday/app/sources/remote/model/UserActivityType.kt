package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class UserActivityType(
    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("type")
    val type: Int? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)

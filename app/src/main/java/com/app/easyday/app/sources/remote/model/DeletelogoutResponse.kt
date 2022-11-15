package com.app.easyday.app.sources.remote.model

import com.google.gson.annotations.SerializedName

data class DeletelogoutResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
) {
    override fun toString(): String {
        return "DeletelogoutResponse(success=$success, message=$message)"
    }
}


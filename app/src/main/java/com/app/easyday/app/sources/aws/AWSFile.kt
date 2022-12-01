package com.app.easyday.app.sources.aws

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState

data class AWSFile(
    val id: Int? = null,
    val newState: TransferState? = null,
    val filename: String? = null
)
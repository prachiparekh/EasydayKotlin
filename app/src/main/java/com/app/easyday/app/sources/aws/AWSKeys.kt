package com.app.easyday.app.sources.aws

import com.amazonaws.regions.Regions

object AWSKeys {
    internal const val COGNITO_POOL_ID =
        "eu-west-1:cd113f0b-00ac-4e5d-ba54-544e448fc522"// Identity pool ID
    internal val MY_REGION = Regions.EU_WEST_1 // WHAT EVER REGION IT MAY BE,PLEASE CHOOSE EXACT
    const val BUCKET_NAME = "easyday-bucket"
    const val FOLDER_NAME_TASK_COMMENT_MEDIA = "/task_comment_media"
    const val FOLDER_NAME_PROFILE_IMAGES = "/profile_images"
    const val FOLDER_NAME_TASK_MEDIA = "/task_media"
}
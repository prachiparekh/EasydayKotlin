package com.app.easyday.app.sources.aws

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ResponseHeaderOverrides
import java.io.File
import java.util.*

object S3Utils {

    @JvmStatic
    fun generates3ShareUrl(
        applicationContext: Context?,
        path: String?,
        folderName: String
    ): String {
        val f = File(path)
        val s3client: AmazonS3 = AmazonUtil.getS3Client(applicationContext)!!
        val expiration = Date()
        var msec = expiration.time
        msec += 1000 * 60 * 60.toLong() // 1 hour.
        expiration.time = msec
        val overrideHeader = ResponseHeaderOverrides()
        overrideHeader.contentType = getMimeType(path)
        val mediaUrl = f.name
        val generatePresignedUrlRequest =
            GeneratePresignedUrlRequest(AWSKeys.BUCKET_NAME + folderName, mediaUrl)
        generatePresignedUrlRequest.method = HttpMethod.GET // Default.
        generatePresignedUrlRequest.expiration = expiration
        generatePresignedUrlRequest.responseHeaders = overrideHeader
        val url = s3client.generatePresignedUrl(generatePresignedUrlRequest)
        Log.e("Generated Url - ", url.toString())
        return url.toString()
    }

    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

}
package com.app.easyday.app.sources.aws

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.app.easyday.app.sources.aws.AWSKeys.FOLDER_NAME_TASK_COMMENT_MEDIA
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

class MultiS3Uploader {
    private var multiS3UploadInterface: MultiS3UploadInterface? = null

    fun setMultiple3UploadDone(s3UploadInterface: MultiS3UploadInterface) {
        this.multiS3UploadInterface = s3UploadInterface
    }

    private fun transferUtility(context: Context): Single<TransferUtility?> {
        return Single.create { emitter ->
            emitter.onSuccess(
                TransferUtility(s3ClientInitialization(context), context)
            )
        }
    }

    private fun s3ClientInitialization(context: Context): AmazonS3 {
        val cognitoCachingCredentialsProvider = CognitoCachingCredentialsProvider(
            context,
            AWSKeys.COGNITO_POOL_ID,  // Identity Pool ID
            AWSKeys.MY_REGION
        )
        return AmazonS3Client(
            cognitoCachingCredentialsProvider,
            Region.getRegion(AWSKeys.MY_REGION)
        )
    }

    fun uploadMultiple(fileToKeyUploads: Map<File, String>, context: Context): Completable {
        return transferUtility(context)
            .flatMapCompletable { transferUtility ->
                Observable.fromIterable(fileToKeyUploads.entries)
                    .flatMapCompletable { entry ->
                        uploadSingle(
                            context,
                            transferUtility,
                            entry.key,
                            entry.value
                        )
                    }
            }

    }

    private fun uploadSingle(
        context: Context,
        transferUtility: TransferUtility,
        aLocalFile: File?,
        toRemoteKey: String?
    ): Completable {
        return Completable.create { emitter ->
            transferUtility.upload(
                AWSKeys.BUCKET_NAME + FOLDER_NAME_TASK_COMMENT_MEDIA,
                toRemoteKey,
                aLocalFile
            )
                .setTransferListener(object : TransferListener {
                    override fun onStateChanged(id: Int, state: TransferState) {
                        if (TransferState.FAILED == state) {
                            emitter.onError(Exception("Transfer state was FAILED."))
                            multiS3UploadInterface?.onUploadError("Transfer state was FAILED.")
                        } else if (TransferState.COMPLETED == state) {
                            emitter.onComplete()
                        }
                        if (state == TransferState.COMPLETED) {


                            multiS3UploadInterface?.onUploadSuccess(
                                S3Utils.generates3ShareUrl(
                                    context,
                                    aLocalFile?.path,
                                    FOLDER_NAME_TASK_COMMENT_MEDIA
                                )
                            )
                        }

                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

                    override fun onError(id: Int, exception: Exception) {

                        emitter.onError(exception)
                        multiS3UploadInterface?.onUploadError(exception.message.toString())
                    }
                })
        }
    }


    interface MultiS3UploadInterface {
        fun onUploadSuccess(response: String?)
        fun onUploadError(response: String?)
    }
}
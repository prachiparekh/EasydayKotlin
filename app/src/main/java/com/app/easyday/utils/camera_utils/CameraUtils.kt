package com.app.easyday.utils.camera_utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.app.easyday.app.sources.local.model.Media
import java.io.File

class CameraUtils {
    companion object{

        var mContext:Context?=null

        val outputDirectory: String by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                "${Environment.DIRECTORY_DCIM}/CameraEasyDay/"
            } else {
                "${mContext?.getExternalFilesDir(Environment.DIRECTORY_DCIM)}/CameraEasyDay/"
            }
        }

        fun getMedia(mContext: Context): List<Media> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getMediaQPlus(mContext)
        } else {
            getMediaQMinus(mContext)
        }.reversed()

        private fun getMediaQPlus(mContext: Context): List<Media> {
            val items = mutableListOf<Media>()
            val contentResolver = mContext.applicationContext.contentResolver

            contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.RELATIVE_PATH,
                    MediaStore.Video.Media.DATE_TAKEN,
                ),
                null,
                null,
                "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val path = cursor.getString(pathColumn)
                    val date = cursor.getLong(dateColumn)

                    val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                    if (path == outputDirectory) {
                        items.add(Media(contentUri, true, date))
                    }
                }
            }

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.RELATIVE_PATH,
                    MediaStore.Images.Media.DATE_TAKEN,
                ),
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val path = cursor.getString(pathColumn)
                    val date = cursor.getLong(dateColumn)

                    val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    if (path == outputDirectory) {
                        items.add(Media(contentUri, false, date))
                    }
                }
            }
            return items
        }

        private fun getMediaQMinus(mContext: Context): List<Media> {
            val items = mutableListOf<Media>()

            File(outputDirectory).listFiles()?.forEach {
                val authority = mContext.applicationContext.packageName + ".provider"
                val mediaUri = FileProvider.getUriForFile(mContext, authority, it)
                items.add(Media(mediaUri, it.extension == "mp4", it.lastModified()))
            }

            return items
        }

    }
}
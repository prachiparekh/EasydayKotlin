package com.app.easyday.screens.activities.main.home.task_detail

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.fetch.VideoFrameUriFetcher
import coil.load
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.TaskMediaItem
import com.app.easyday.utils.camera_utils.TaskMediaDiffCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions

class TaskMediaAdapter(
    val mContext: Context,
    private val onItemClick: (Boolean, String) -> Unit
) : ListAdapter<TaskMediaItem, TaskMediaAdapter.PicturesViewHolder>(TaskMediaDiffCallback()) {

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PicturesViewHolder(inflater.inflate(R.layout.item_picture, parent, false))

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class PicturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagePreview: ImageView = itemView.findViewById(R.id.imagePreview)
        private val imagePlay: ImageView = itemView.findViewById(R.id.imagePlay)

        fun bind(item: TaskMediaItem) {


            val isImg  =
                item.mediaUrl?.endsWith(".jpg") == true ||
                        item.mediaUrl?.endsWith(".jpeg") == true || item.mediaUrl?.endsWith(
                    ".png"
                ) == true

            imagePreview.load(item.mediaUrl) {
                if (!isImg
                ) {
                    imagePlay.isVisible = true
                    fetcher(VideoFrameUriFetcher(itemView.context))

                    val options = RequestOptions()
                    Glide.with(mContext)
                        .load(item.mediaUrl?.let { ThumbnailUtils.createVideoThumbnail(it, MediaStore.Video.Thumbnails.MINI_KIND) })
                        .apply(
                            options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.HIGH)
                                .format(DecodeFormat.PREFER_ARGB_8888)
                        )
                        .thumbnail(Glide.with(mContext).load(item.mediaUrl))
                        .into(imagePreview)


                }else {
                    imagePlay.isVisible = false

                    val options = RequestOptions()
                    Glide.with(mContext)
                        .load(item.mediaUrl)
                        .placeholder(R.drawable.ic_gradient_top_bg)
                        .apply(
                            options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.HIGH)
                                .format(DecodeFormat.PREFER_ARGB_8888)
                        )
                        .into(imagePreview)
                }
            }
            imagePreview.setOnClickListener {

                item.mediaUrl?.let { it1 ->
                    onItemClick(
                        isImg,
                        it1
                    )
                }
            }
        }
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath, HashMap())
        return retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    }
}

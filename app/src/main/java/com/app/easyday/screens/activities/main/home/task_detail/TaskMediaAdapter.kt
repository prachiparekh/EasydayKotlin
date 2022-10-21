package com.app.easyday.screens.activities.main.home.task_detail

import android.content.Context
import android.util.Log
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
                    imagePlay.isVisible=true
                    fetcher(VideoFrameUriFetcher(itemView.context))
                }else{
                    imagePlay.isVisible=false
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
}

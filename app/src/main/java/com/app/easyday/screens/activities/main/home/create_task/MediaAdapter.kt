package com.app.easyday.screens.activities.main.home.create_task

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.fetch.VideoFrameUriFetcher
import coil.load
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.Media
import com.app.easyday.utils.camera_utils.MediaDiffCallback

class MediaAdapter(
    val mContext:Context,
    private val onItemClick: (Boolean, Uri) -> Unit,
    private val onDeleteClick: (Boolean, Uri) -> Unit
) : ListAdapter<Media, MediaAdapter.PicturesViewHolder>(MediaDiffCallback()) {

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PicturesViewHolder(inflater.inflate(R.layout.item_picture, parent, false))

    override fun onBindViewHolder(holder: PicturesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun deleteImage(currentPage: Int) {
        if (currentPage < itemCount) {
            val media = getItem(currentPage)
            val allMedia = currentList.toMutableList()
            allMedia.removeAt(currentPage)
            submitList(allMedia)
            media.uri?.let { onDeleteClick(allMedia.size == 0, it) }
        }
    }

    inner class PicturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagePreview: ImageView = itemView.findViewById(R.id.imagePreview)
        private val imagePlay: ImageView = itemView.findViewById(R.id.imagePlay)

        fun bind(item: Media) {
            imagePlay.visibility = if (item.isVideo) View.VISIBLE else View.GONE
            imagePreview.load(item.uri) {
                if (item.isVideo) {
                    fetcher(VideoFrameUriFetcher(itemView.context))
                }
            }
            imagePreview.setOnClickListener { item.uri?.let { it1 ->
                onItemClick(item.isVideo,
                    it1
                )
            } }
        }
    }
}

package com.app.easyday.screens.activities.main.home.create_task

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.fetch.VideoFrameUriFetcher
import coil.load
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.TaskMediaItem
import com.app.easyday.screens.dialogs.VideoPlayBottomSheetDialog
import com.app.easyday.utils.camera_utils.TaskMediaDiffCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class TaskMediaAdapter(
    val mContext: Context, val activity: Activity, val manager: FragmentManager,
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
        private val customImagePlay: ImageView = itemView.findViewById(R.id.custom_imagePlay)
        private val player: PlayerView = itemView.findViewById(R.id.exo_player_view)
        private val vidDuration: TextView = itemView.findViewById(R.id.duration)
        private val videoOptions: RelativeLayout = itemView.findViewById(R.id.video_RL)
        private val fullScreen: ImageView = itemView.findViewById(R.id.fullScreen)
        private val resize: ImageView = itemView.findViewById(R.id.fullScreen_video)

        lateinit var simpleExoPlayer: SimpleExoPlayer

        fun bind(item: TaskMediaItem) {

            Log.e("item", item.toString())
            val isImg  =
                item.mediaUrl?.endsWith(".jpg") == true ||
                        item.mediaUrl?.endsWith(".jpeg") == true || item.mediaUrl?.endsWith(
                    ".png"
                ) == true
//            Log.e("text", "video Playing")
            imagePreview.load(item.mediaUrl) {
                if (!isImg
                ) {
                    imagePreview.isVisible = true
                    imagePlay.isVisible = true
                    vidDuration.text = item.duration.toString()

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
                    videoOptions.isVisible = false
                    player.isVisible = false

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


            imagePlay.setOnClickListener {
//                Toast.makeText(mContext, "video Playing", Toast.LENGTH_SHORT).show()
                imagePreview.isVisible = false
                imagePlay.isVisible = false
                videoOptions.isVisible = false
                player.isVisible = true
                playVideo(item.mediaUrl)
//                Log.e("text", "video Playing")
//                item.mediaUrl?.let { it1 -> onItemClick(isImg, it1) }
            }
//            imagePreview.setOnClickListener {
//
//                item.mediaUrl?.let { it1 ->
//                    onItemClick(
//                        isImg,
//                        it1
//                    )
//                }
//            }
            customImagePlay.setOnClickListener {
                pauseVideo()
            }

            fullScreen.setOnClickListener {
                simpleExoPlayer.pause()
                customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))
                simpleExoPlayer.playWhenReady = false

                val fragment = VideoPlayBottomSheetDialog(item.mediaUrl, simpleExoPlayer, mContext, activity)
                fragment.show(manager, "")

            }
        }

        private fun pauseVideo() {
            if (simpleExoPlayer.isPlaying) {
                simpleExoPlayer.pause()
                customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))
                simpleExoPlayer.playWhenReady = false
            } else {
                simpleExoPlayer.play()
                simpleExoPlayer.playWhenReady = true
                customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_pause_circle))
            }
//            simpleExoPlayer.pause()
//            customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))
        }
        private fun playVideo(mediaUrl: String?) {

            simpleExoPlayer = SimpleExoPlayer.Builder(mContext)
                .setSeekForwardIncrementMs(1000)
                .setSeekBackIncrementMs(1000)
                .build()
            player.player = simpleExoPlayer
            player.keepScreenOn = true
            simpleExoPlayer.addListener(object : Player.Listener{
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE){
                        customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))

                        player.keepScreenOn = false
                        simpleExoPlayer.playWhenReady = false
                    }
                }
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(mContext, "Video Playing Error", Toast.LENGTH_SHORT)
                        .show()
                    simpleExoPlayer.playWhenReady = false
                    customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))
                }
            })
            val videoSource = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")
            val mediaItem = MediaItem.fromUri(videoSource)
//            val mediaItem = mediaUrl?.let { MediaItem.fromUri(it) }
            if (mediaItem != null) {
                simpleExoPlayer.setMediaItem(mediaItem)
            }
            simpleExoPlayer.prepare()
            simpleExoPlayer.play()
//            playError()
        }

        private fun playError() {
            simpleExoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(mContext, "Video Playing Error", Toast.LENGTH_SHORT)
                        .show()
                    simpleExoPlayer.playWhenReady = false
                    customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))
                }
            })
            simpleExoPlayer.playWhenReady = true
            customImagePlay.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_pause_circle))
        }
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String?): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath, HashMap())
        return retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
    }
}

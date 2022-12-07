package com.app.easyday.screens.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.databinding.VideoPlayBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.custom_video_controls.view.*

class VideoPlayBottomSheetDialog(
    val mediaUrl: String?,
    var simpleExoPlayer: SimpleExoPlayer,
    val mContext: Context,
    val activity: Activity
) :
    BottomSheetDialogFragment() {
    var binding: VideoPlayBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.video_play, container, false)
        isCancelable = false

//        val orientation = activity.resources.configuration.orientation
        val orientation: Int = activity.resources.configuration.orientation

        val player = binding?.exoPlayerView
        val playBtn = binding?.exoPlayerView?.custom_imagePlay
        val fullSc = binding?.exoPlayerView?.fullScreen
        val back = binding?.exoPlayerView?.back
        val progressB = binding?.exoPlayerView?.exo_progress
        playBtn?.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_circle))

        player?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

        simpleExoPlayer = SimpleExoPlayer.Builder(mContext)
            .setSeekForwardIncrementMs(1000)
            .setSeekBackIncrementMs(1000)
            .build()
        player?.player = simpleExoPlayer
        player?.keepScreenOn = true
        simpleExoPlayer.addListener(object : Player.Listener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE){
                    playBtn?.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))

                    player?.keepScreenOn = false
                    simpleExoPlayer.playWhenReady = false
                }
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

        back?.isVisible = true
        progressB?.isVisible = true
        back?.setOnClickListener {
//            when(orientation){
//                Configuration.ORIENTATION_LANDSCAPE -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                else -> {
//                    dismiss()
//                }
//            }
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            else if (orientation == Configuration.ORIENTATION_PORTRAIT)
                dismiss()

        }

        playBtn?.setOnClickListener {
            if (simpleExoPlayer.isPlaying) {
                simpleExoPlayer.pause()
                playBtn.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_play_circle))
                simpleExoPlayer.playWhenReady = false
            } else {
                simpleExoPlayer.play()
                simpleExoPlayer.playWhenReady = true
                playBtn.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_pause_circle))
            }
        }
        val isProtrait = true
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        fullSc?.setOnClickListener {

//            if (isProtrait){
//                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            }else
//                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            Toast.makeText(mContext, "portrait", Toast.LENGTH_SHORT).show()


            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                //set in landscape
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                //set in portrait
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        }

        binding?.root?.isFocusableInTouchMode = true
        binding?.root?.requestFocus()
        binding?.root?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                else if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    dismiss()

                true
            } else false
        }


        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it1 ->
                val behaviour = BottomSheetBehavior.from(it1)
                setupFullHeight(it1)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED

                behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                        // React to dragging events
                    }
                })
            }

        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}

package vn.icheck.android.helper

import android.net.Uri
import android.view.SurfaceView
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseVideoViewHolder
import vn.icheck.android.ichecklibs.util.getString

object ExoPlayerManager {
    var manager = mutableListOf<SimpleExoPlayer>()
    var currentPosition = 0

    var player: SimpleExoPlayer? = null
    private var currentSurfaceView: SurfaceView? = null

    private fun setupPlayer() {
        if (player == null) {
            player = SimpleExoPlayer.Builder(ICheckApplication.getInstance()).build().apply {
                playWhenReady = false
                repeatMode = Player.REPEAT_MODE_OFF
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;
            }
        }

        player?.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)

                if (playbackState == Player.STATE_READY) {
                    currentSurfaceView?.visibility = View.VISIBLE
                } else {
                    currentSurfaceView?.visibility = View.INVISIBLE
                }
            }
        })
    }

    fun playVideo(surfaceView: SurfaceView, url: String) {
        setupPlayer()

        if (currentSurfaceView != surfaceView) { //  || player?.playbackState == Player.STATE_ENDED
            stopVideo()
            val dataSourceFactory = DefaultDataSourceFactory(ICheckApplication.getInstance(), Util.getUserAgent(ICheckApplication.getInstance(), getString(R.string.app_name)))
            player?.prepare(ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url)))
            player?.setVideoSurfaceView(surfaceView)
            player?.playWhenReady = true
            currentSurfaceView = surfaceView
        }
    }

    fun stopVideo() {
        if (currentSurfaceView != null) {
            player?.clearVideoSurfaceView(currentSurfaceView)
            player?.playWhenReady = false
            currentSurfaceView?.visibility = View.VISIBLE
            currentSurfaceView?.visibility = View.INVISIBLE
            currentSurfaceView = null
        }
    }

    fun checkPlayVideoBase(recyclerView: RecyclerView, prefixHeight: Int = 0) {
        recyclerView.layoutManager.let { layoutManager ->
            if (layoutManager is LinearLayoutManager) {
                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                for (position in firstPosition until firstPosition + 10) {
                    recyclerView.findViewHolderForAdapterPosition(position)?.let { viewHolder ->
                        if (viewHolder is BaseVideoViewHolder) {
                            if (viewHolder.itemView.y > prefixHeight && (viewHolder.itemView.y + viewHolder.itemView.height) <= recyclerView.height) {
                                if (viewHolder.onPlayVideo()) {
                                    return
                                }
                            }
                        }
                    }
                }
                stopVideo()
            }
        }
    }

    fun checkPlayVideoHorizontal(recyclerView: RecyclerView): Boolean {
        recyclerView.layoutManager.let { layoutManager ->
            if (layoutManager is LinearLayoutManager) {
                for (position in layoutManager.findFirstVisibleItemPosition() until layoutManager.findLastVisibleItemPosition() + 1) {
                    recyclerView.findViewHolderForAdapterPosition(position)?.let { viewHolder ->
                        if (viewHolder is BaseVideoViewHolder) {
                            if (viewHolder.itemView.x > 0 && (viewHolder.itemView.x + viewHolder.itemView.width) < recyclerView.width) {
                                if (viewHolder.onPlayVideo()) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }
}
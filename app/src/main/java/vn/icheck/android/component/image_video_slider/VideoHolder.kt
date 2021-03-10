package vn.icheck.android.component.image_video_slider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.video_holder.view.*
import vn.icheck.android.R

class VideoHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(mediaLogic: MediaLogic) {
//        val df = DefaultDataSourceFactory(view.context, Util.getUserAgent(view.context, "iCheck"))
//        val ms = ProgressiveMediaSource.Factory(df)
//                .createMediaSource(Uri.parse(icMedia.src))
        if (mediaLogic.progressiveMediaSource != null) {
            mediaLogic.exoPlayer?.playWhenReady = true
            mediaLogic.exoPlayer?.prepare(mediaLogic.progressiveMediaSource!!, false, true)
            mediaLogic.exoPlayer?.volume = 0f
            mediaLogic.exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE
            mediaLogic.exoPlayer?.setVideoTextureView(view.vid)
            mediaLogic.exoPlayer?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            mediaLogic.exoPlayer?.setWakeMode(C.WAKE_MODE_NETWORK)
//            exoPlayer.setWakeMode(C.WAKE_MODE_NETWORK)
//            view.vid.player = exoPlayer
//            view.vid.setShutterBackgroundColor(Color.TRANSPARENT)
//            exoPlayer.seekTo(adapterPosition, C.TIME_UNSET)
      
        }
    }


    companion object {
        fun create(parent: ViewGroup): VideoHolder {
            val lf = LayoutInflater.from(parent.context)
            val v = lf.inflate(R.layout.video_holder, parent, false)
            return VideoHolder(v)
        }
    }
}
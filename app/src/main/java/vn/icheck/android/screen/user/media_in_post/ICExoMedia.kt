package vn.icheck.android.screen.user.media_in_post

import android.net.Uri
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant


class ICExoMedia(val src: String?, val type: String?) {
    var progressiveMediaSource: MediaSource? = null
    var exoPlayer: SimpleExoPlayer? = null
    var mediaError: Boolean = false
    var resetImage: Boolean = false


    //Minimum Video you want to buffer while Playing
    private val MIN_BUFFER_DURATION = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS

    //Max Video you want to buffer during PlayBack
    private val MAX_BUFFER_DURATION = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS

    //Min Video you want to buffer before start Playing it
    private val MIN_PLAYBACK_START_BUFFER = 1500

    //Min video You want to buffer when user resumes video
    private val MIN_PLAYBACK_RESUME_BUFFER = 2000
    fun checkTypeMedia() {
        if (type == Constant.VIDEO) {
            //exoPlayer=SimpleExoPlayer.Builder(ICheckApplication.getInstance()).build()

            val trackSelector: TrackSelector = DefaultTrackSelector()

            val loadControl: LoadControl = DefaultLoadControl.Builder()
                    .setAllocator(DefaultAllocator(true, 16))
                    .setBufferDurationsMs(MIN_BUFFER_DURATION,
                            MAX_BUFFER_DURATION,
                            MIN_PLAYBACK_START_BUFFER,
                            MIN_PLAYBACK_RESUME_BUFFER)
                    .setTargetBufferBytes(-1)
                    .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()
            exoPlayer = ExoPlayerFactory.newSimpleInstance(ICheckApplication.getInstance(), trackSelector, loadControl)
            progressiveMediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(ICheckApplication.getInstance(), Util.getUserAgent(ICheckApplication.getInstance(), ICheckApplication.getInstance().getString(R.string.app_name))))
                    .createMediaSource(Uri.parse(src))
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
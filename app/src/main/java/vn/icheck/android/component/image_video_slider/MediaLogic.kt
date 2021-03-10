package vn.icheck.android.component.image_video_slider

import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.Serializable

class MediaLogic(val src: String?, val type: Int) {
    var idHolder: Int? = null
    var progressiveMediaSource: MediaSource? = null
    var exoPlayer: SimpleExoPlayer? = null
    var bgDefault: Int? = null
    fun setMs(dataSourceFactory: DefaultDataSourceFactory) {
        progressiveMediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(src))
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
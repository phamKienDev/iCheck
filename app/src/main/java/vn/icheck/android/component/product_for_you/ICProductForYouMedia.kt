package vn.icheck.android.component.product_for_you

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICProductForYou

class ICProductForYouMedia(val product: ICProductForYou) {
    var progressiveMediaSource: MediaSource? = null
    var exoPlayer: SimpleExoPlayer? = null

    fun checkTypeMedia(context: Context) {
        if (product.media.type != Constant.IMAGE) {
            exoPlayer = SimpleExoPlayer.Builder(context).build()
            progressiveMediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context, Util.getUserAgent(context, "icheck")))
                    .createMediaSource(Uri.parse(product.media.content))
        }
    }
    fun release() {
        exoPlayer?.release()
    }
}
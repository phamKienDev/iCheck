package vn.icheck.android.base.holder

import android.view.SurfaceView
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import org.jetbrains.annotations.NotNull
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IAdsListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemAdsPageBinding
import vn.icheck.android.helper.ExoPlayerManager
import vn.icheck.android.network.models.ICMedia

abstract class BaseVideoViewHolder(view: View) : RecyclerView.ViewHolder(view), IAdsListener {

    fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_UPDATE_AUTO_PLAY_VIDEO))
                }
            }
        })
    }

    fun playVideo(surfaceView: SurfaceView, media: MutableList<ICMedia>?) : Boolean {
        if (media?.get(0)?.type == Constant.VIDEO) {
            media[0].content?.let { ExoPlayerManager.playVideo(surfaceView, it) }
            return true
        } else {
            ExoPlayerManager.stopVideo()
            return false
        }
    }
}
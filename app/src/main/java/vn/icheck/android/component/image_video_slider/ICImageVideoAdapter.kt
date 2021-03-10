package vn.icheck.android.component.image_video_slider

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.util.ick.logDebug

class ICImageVideoAdapter(val listData: List<MediaLogic>, val onClick:(() -> Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    init {
//        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICMediaType.TYPE_IMAGE -> ImageHolder.create(parent)
            ICMediaType.TYPE_VIDEO -> VideoHolder.create(parent)
            else -> NullHolder.create(parent)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            ICMediaType.TYPE_IMAGE -> (holder as ImageHolder).bind(listData[position], listData, onClick)
            ICMediaType.TYPE_VIDEO -> (holder as VideoHolder).bind(listData[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].type
    }

}
package vn.icheck.android.screen.user.media_in_post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.ic_image_holder.view.*
import kotlinx.android.synthetic.main.ic_image_holder2.view.*
import kotlinx.android.synthetic.main.video_detail_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.ichecklibs.util.showLongErrorToast
import vn.icheck.android.util.kotlin.WidgetUtils

class MediaInPostAdapter(val isFullMedia: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val videoType = 1
    private val imageType = 2

    val listData = mutableListOf<ICExoMedia>()

    fun setData(list: MutableList<ICExoMedia>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == videoType) {
            VideoHolder(parent)
        } else {
            if (isFullMedia)
                ImageFullHolder(LayoutInflater.from(parent.context).inflate(R.layout.ic_image_holder2, parent, false))
            else
                ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.ic_image_holder, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].type == Constant.VIDEO) {
            videoType
        } else if (listData[position].type == Constant.IMAGE) {
            imageType
        } else {
            super.getItemViewType(position)
        }
    }

    val getListData: MutableList<ICExoMedia>
        get() = listData

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoHolder -> {
                holder.bind(listData[position])
            }
            is ImageHolder -> {
                holder.bind(listData[position])
            }
            is ImageFullHolder -> {
                holder.bind(listData[position])
            }
        }
    }

    inner class VideoHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.video_detail_holder, parent, false)) {
        fun bind(media: ICExoMedia) {
            if (media.progressiveMediaSource != null) {
//                media.exoPlayer?.playWhenReady = true
                media.exoPlayer?.prepare(media.progressiveMediaSource!!, false, true)
                media.exoPlayer?.volume = 1f
                media.exoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
                itemView.videoView.player = media.exoPlayer
                media.exoPlayer?.setWakeMode(C.WAKE_MODE_NETWORK)

                itemView.progress.visibility = View.VISIBLE
                itemView.tvError.visibility = View.GONE

                media.exoPlayer?.addListener(object : Player.EventListener {
                    override fun onPlayerError(error: ExoPlaybackException) {
                        super.onPlayerError(error)
                        itemView.context.showLongErrorToast(itemView.context.getString(R.string.trinh_phat_video_loi))
                        itemView.progress.visibility = View.GONE
                        media.mediaError = true
                    }


                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        super.onPlayerStateChanged(playWhenReady, playbackState)
                        when (playbackState) {
                            Player.STATE_IDLE -> {
                                itemView.progress.visibility = View.GONE
                                itemView.context.showLongErrorToast(itemView.context.getString(R.string.trinh_phat_video_loi))
                            }
                            Player.STATE_BUFFERING -> {
                                itemView.progress.visibility = View.VISIBLE
                            }
                            Player.STATE_READY -> {
                                itemView.progress.visibility = View.GONE
                                media.mediaError = false
                            }
                        }
                    }
                })
            }
        }
    }

    inner class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(media: ICExoMedia) {
            WidgetUtils.loadImageUrlNotTransform(itemView.src_image, media.src, R.drawable.img_default_loading_icheck)
        }
    }

    inner class ImageFullHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(media: ICExoMedia) {
            WidgetUtils.loadImageUrlNotTransform(itemView.imgFull, media.src, R.drawable.img_default_loading_icheck)
            if (media.resetImage) {
                itemView.imgFull.resetZoom()
                media.resetImage = false
            }
        }
    }

}
package vn.icheck.android.chat.icheckchat.screen.detail_image

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.item_media_detail.view.*
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.ConstantChat.VIDEO
import vn.icheck.android.chat.icheckchat.base.recyclerview.BaseRecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.BaseViewHolder
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_IMAGE
import vn.icheck.android.chat.icheckchat.base.view.MCViewType.TYPE_VIDEO
import vn.icheck.android.chat.icheckchat.base.view.loadImageUrlNotCrop
import vn.icheck.android.chat.icheckchat.databinding.ItemMediaDetailBinding
import vn.icheck.android.chat.icheckchat.databinding.ItemVideoDetailBinding
import vn.icheck.android.chat.icheckchat.helper.MCExoMedia
import vn.icheck.android.chat.icheckchat.model.MCMedia
import vn.icheck.android.ichecklibs.ViewHelper

class ImageDetailAdapter : BaseRecyclerView<MCExoMedia>() {

    fun setData(obj: MutableList<MCExoMedia>) {
        listData.clear()

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return if (listData[position].type == VIDEO) {
            TYPE_VIDEO
        } else {
            TYPE_IMAGE
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_VIDEO) {
            VideoDetailHolder(ItemVideoDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ImageDetailHolder(ItemMediaDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageDetailHolder -> {
                holder.bind(listData[position])
            }
            is VideoDetailHolder -> {
                holder.bind(listData[position])
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    inner class ImageDetailHolder(val binding: ItemMediaDetailBinding) : BaseViewHolder<MCExoMedia>(binding) {
        override fun bind(obj: MCExoMedia) {
            loadImageUrlNotCrop(binding.imgFull, obj.src, R.drawable.ic_default_image_upload_150_chat)

            if (obj.resetImage) {
                itemView.imgFull.resetZoom()
                obj.resetImage = false
            }
        }
    }

    inner class VideoDetailHolder(val binding: ItemVideoDetailBinding) : BaseViewHolder<MCExoMedia>(binding) {
        override fun bind(obj: MCExoMedia) {
            binding.tvError.background=ViewHelper.bgAccentRedCorners24(itemView.context)

            if (obj.progressiveMediaSource != null) {
//                media.exoPlayer?.playWhenReady = true
                obj.exoPlayer?.prepare(obj.progressiveMediaSource!!, false, true)
                obj.exoPlayer?.volume = 1f
                obj.exoPlayer?.repeatMode = Player.REPEAT_MODE_OFF
                binding.videoView.player = obj.exoPlayer
                obj.exoPlayer?.setWakeMode(C.WAKE_MODE_NETWORK)

                binding.progress.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE

                obj.exoPlayer?.addListener(object : Player.EventListener {
                    override fun onPlayerError(error: ExoPlaybackException) {
                        super.onPlayerError(error)
                        binding.tvError.visibility = View.VISIBLE
                        binding.progress.visibility = View.GONE
                        obj.mediaError = true
                    }


                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        super.onPlayerStateChanged(playWhenReady, playbackState)
                        when (playbackState) {
                            Player.STATE_IDLE -> {
                                binding.progress.visibility = View.GONE
                                binding.tvError.visibility = View.VISIBLE
                            }
                            Player.STATE_BUFFERING -> {
                                binding.progress.visibility = View.VISIBLE
                                binding.tvError.visibility = View.GONE
                            }
                            Player.STATE_READY -> {
                                binding.progress.visibility = View.GONE
                                binding.tvError.visibility = View.GONE
                                obj.mediaError = false
                            }
                        }
                    }
                })
            }
        }
    }
}
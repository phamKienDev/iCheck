package vn.icheck.android.component.product_for_you

import android.graphics.Color
import android.graphics.Paint
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.item_list_product_for_you.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class ListProductForYouAdapter(val listData: MutableList<ICProductForYouMedia>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemProductForYouHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemProductForYouHolder).bind(listData[position])
    }

    inner class ItemProductForYouHolder(parent: ViewGroup) : BaseViewHolder<ICProductForYouMedia>(LayoutInflater.from(parent.context).inflate(R.layout.item_list_product_for_you, parent, false)) {
        override fun bind(obj: ICProductForYouMedia) {

            if (obj.product.media.type == Constant.VIDEO) {
                itemView.texture_media.visibility = View.VISIBLE
                if (obj.progressiveMediaSource != null) {
                    obj.exoPlayer?.repeatMode = Player.REPEAT_MODE_ONE
                    obj.exoPlayer?.prepare(obj.progressiveMediaSource!!, false, true)
                    obj.exoPlayer?.setVideoTextureView(itemView.texture_media)
                    obj.exoPlayer?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                    obj.exoPlayer?.setWakeMode(C.WAKE_MODE_NETWORK)
                    obj.exoPlayer?.volume = 0f

                    obj.exoPlayer?.addListener(object : Player.EventListener {
                        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                            super.onPlayerStateChanged(playWhenReady, playbackState)
                            when (playbackState) {
                                Player.STATE_BUFFERING -> {
                                    itemView.progress.visibility = View.VISIBLE
                                }
                                Player.STATE_READY -> {
                                    itemView.progress.visibility = View.GONE
                                }
                                else -> {
                                    itemView.img_media.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_default_square))
                                }
                            }
                        }

                    })
                }
            } else {
                itemView.texture_media.visibility = View.INVISIBLE
                itemView.progress.visibility = View.GONE
                if (obj.product.media.content != null) {
                    WidgetUtils.loadImageUrl(itemView.img_media, obj.product.media.content)
                } else {
                    WidgetUtils.loadImageUrl(itemView.img_media, "")
                }
            }

            if (obj.product.owner.avatar != null) {
                WidgetUtils.loadImageUrl(itemView.img_logo, obj.product.owner.avatar)
            } else {
                WidgetUtils.loadImageUrl(itemView.img_logo, "")
            }

            itemView.tv_name_product.text = if (obj.product.name != null) {
                obj.product.name
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }

            itemView.tv_name_enterprise.text = if (obj.product.owner.name != null) {
                obj.product.owner.name
            } else {
                itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (obj.product.rating != null) {
                itemView.tv_count_rating.text = obj.product.rating.toString()

                setRating(obj.product.rating.toString().toFloat())
            }

            if (obj.product.bestPrice > 0L) {
                itemView.tv_special_price.visibility = View.VISIBLE
                itemView.tv_special_price.text = Html.fromHtml(itemView.context.getString(R.string.xxx___d, TextHelper.formatMoney(obj.product.price)))
                itemView.tv_special_price.paintFlags = itemView.tv_special_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                itemView.tv_price.text = Html.fromHtml(itemView.context.getString(R.string.xxx___d, TextHelper.formatMoney(obj.product.bestPrice)))
            } else {
                itemView.tv_special_price.visibility = View.GONE
                itemView.tv_price.text = Html.fromHtml(itemView.context.getString(R.string.xxx___d, TextHelper.formatMoney(obj.product.price)))
            }


        }

        private fun setRating(rating_count: Float) {
            when {
                rating_count < 6 -> {
                    itemView.tv_rating.text = itemView.context.getString(R.string.x_diem_danh_gia)
                    itemView.tv_rating.setTextColor(ContextCompat.getColor(itemView.context, R.color.violet))
                    itemView.tv_rating.background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(itemView.context, R.color.violet), SizeHelper.size14.toFloat())
                }
                rating_count < 7 -> {
                    itemView.tv_rating.text = itemView.context.getString(R.string.x_hai_long)
                    itemView.tv_rating.setTextColor(ContextCompat.getColor(itemView.context, R.color.green_v2))
                    itemView.tv_rating.background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(itemView.context, R.color.green_v2), SizeHelper.size14.toFloat())
                }
                rating_count < 8 -> {
                    itemView.tv_rating.text = itemView.context.getString(R.string.x_tot)
                    itemView.tv_rating.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange_v2))
                    itemView.tv_rating.background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(itemView.context, R.color.orange_v2), SizeHelper.size14.toFloat())
                }
                rating_count < 9 -> {
                    itemView.tv_rating.text = itemView.context.getString(R.string.x_tuyet_voi)
                    itemView.tv_rating.setTextColor(ContextCompat.getColor(itemView.context, R.color.red_v2))
                    itemView.tv_rating.background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(itemView.context, R.color.red_v2), SizeHelper.size14.toFloat())
                }
                else -> {
                    itemView.tv_rating.text = itemView.context.getString(R.string.x_tren_ca_tuyet_voi)
                    itemView.tv_rating.setTextColor(ContextCompat.getColor(itemView.context, R.color.lightBlue))
                    itemView.tv_rating.background = ViewHelper.createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, ContextCompat.getColor(itemView.context, R.color.lightBlue), SizeHelper.size14.toFloat())
                }
            }
        }
    }


}
package vn.icheck.android.loyalty.screen.game_from_labels.game_list

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_game_loyalty.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKGame
import vn.icheck.android.loyalty.screen.web.WebViewActivity
import vn.icheck.android.loyalty.sdk.CampaignType
import java.text.SimpleDateFormat
import java.util.*

internal class GameFromLabelsListAdapter(callback: IRecyclerViewCallback, val clickListener: IClickListener) : RecyclerViewCustomAdapter<ICKGame>(callback) {
    override fun getItemType(position: Int): Int {
        return ICKViewType.ITEM_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_game_loyalty, parent, false)) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val show = SimpleDateFormat("dd/MM", Locale.getDefault())

        @SuppressLint("SetTextI18n")
        fun bind(obj: ICKGame) {
            WidgetHelper.loadImageUrl(itemView.imgAvatar, obj.owner?.logo?.medium)
            WidgetHelper.loadImageUrl(itemView.imgBanner, obj.image?.original, R.drawable.ic_default_img_game)

            val startDate = sdf.parse(obj.startAt)
            val endDate = sdf.parse(obj.endAt)
            val timeString = "${show.format(startDate)} - ${show.format(endDate)}"

            itemView.tvName.text = if (!obj.owner?.name.isNullOrEmpty()) {
                obj.owner?.name
            } else {
                "Đang cập nhật"
            }

            if (obj.statusTime != "RUNNING") {
                itemView.imgUpcoming.setVisible()
                itemView.layoutRight.setInvisible()
                itemView.tvDateRight.setInvisible()
                itemView.tvDate.setVisible()

                itemView.tvDate.text = timeString
            } else {
                itemView.tvDate.visibility = View.GONE
                itemView.imgUpcoming.setInvisible()
                itemView.layoutRight.setVisible()
                itemView.tvDateRight.setVisible()
                itemView.tvPlay.setGone()
                itemView.tvPoint.setGone()

                when (obj.type) {
                    CampaignType.ACCUMULATE_POINT -> {
                        itemView.tvPoint.setVisible()
                        itemView.tvPlay.setGone()

                        if (!obj.statisticWinnerAccumulatePoint.isNullOrEmpty()) {
                            if (obj.statisticWinnerAccumulatePoint?.get(0)?.points != null) {
                                itemView.tvPoint.text = "${obj.statisticWinnerAccumulatePoint?.get(0)?.points} Điểm"
                            } else {
                                itemView.tvPoint.text = "${0} Điểm"
                            }
                        } else {
                            itemView.tvPoint.text = "${0} Điểm"
                        }
                    }
                    CampaignType.RECEIVE_GIFT -> {

                    }
                    CampaignType.MINI_GAME, CampaignType.MINI_GAME_QR_MAR -> {
                        itemView.tvPoint.setGone()
                        itemView.tvPlay.setVisible()

                        if (!obj.campaignGameUser.isNullOrEmpty()) {
                            if (obj.campaignGameUser[0]?.play!! > 0) {
                                itemView.tvPlay.setTextColor(Color.parseColor("#057DDA"))
                                itemView.tvPlay.text = "${obj.campaignGameUser?.get(0)?.play} lượt quay"
                            } else {
                                itemView.tvPlay.setTextColor(Color.parseColor("#828282"))
                                itemView.tvPlay.text = "Hết lượt quay"
                            }
                        } else {
                            itemView.tvPlay.setTextColor(Color.parseColor("#828282"))
                            itemView.tvPlay.text = "Hết lượt quay"
                        }
                    }
                    else -> {

                    }
                }

                itemView.tvDateRight.text = timeString
            }

            itemView.setOnClickListener {

                if (obj.type == "mini_game_qr_mar") {
                    SharedLoyaltyHelper(itemView.context).putBoolean(CampaignType.ACCUMULATE_LONG_TERM_POINT_QR_MAR, true)
                } else {
                    SharedLoyaltyHelper(itemView.context).putBoolean(CampaignType.ACCUMULATE_LONG_TERM_POINT_QR_MAR, false)
                }

                clickListener.onClick(obj)
            }
        }
    }
}
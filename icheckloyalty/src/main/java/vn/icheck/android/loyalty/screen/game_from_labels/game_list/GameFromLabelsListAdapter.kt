package vn.icheck.android.loyalty.screen.game_from_labels.game_list

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_game_loyalty.view.*
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKGame
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

            itemView.tvName.apply {
                text = if (!obj.owner?.name.isNullOrEmpty()) {
                    obj.owner?.name
                } else {
                    context.getString(R.string.dang_cap_nhat)
                }
            }

            if (obj.statusTime != "RUNNING") {
                itemView.imgUpcoming.setVisible()
                itemView.layoutRight.setInvisible()
                itemView.tvDateRight.setInvisible()
                itemView.tvDate.setVisible()

                itemView.tvDate.text = timeString
            } else {
                itemView.tvDate.visibility = View.GONE

                when (obj.type) {
                    "accumulate_point" -> {
                        itemView.imgUpcoming.setInvisible()
                        itemView.layoutRight.setVisible()
                        itemView.tvPoint.setVisible()
                        itemView.tvPlay.setGone()
                        itemView.tvDateRight.setVisible()

                        if (!obj.statisticWinnerAccumulatePoint.isNullOrEmpty()) {
                            if (obj.statisticWinnerAccumulatePoint[0].points != null) {
                                itemView.tvPoint.setText(R.string.d_diem,
                                    obj.statisticWinnerAccumulatePoint[0].points
                                )
                            } else {
                                itemView.tvPoint.setText(R.string.d_diem, 0)
                            }
                        } else {
                            itemView.tvPoint.setText(R.string.d_diem, 0)
                        }
                    }
                    "receive_gift" -> {
                        itemView.imgUpcoming.setInvisible()
                        itemView.layoutRight.setInvisible()
                        itemView.tvDateRight.setInvisible()
                    }
                    "mini_game" -> {
                        itemView.imgUpcoming.setInvisible()
                        itemView.layoutRight.setVisible()
                        itemView.tvPoint.setGone()
                        itemView.tvPlay.setVisible()
                        itemView.tvDateRight.setVisible()

                        if (!obj.campaignGameUser.isNullOrEmpty()) {
                            if (obj.campaignGameUser[0]?.play!! > 0) {
                                itemView.tvPlay.setTextColor(Color.parseColor("#057DDA"))
                                itemView.tvPlay.setText(R.string.d_luot_quay, obj.campaignGameUser[0]?.play)
                            } else {
                                itemView.tvPlay.setTextColor(Color.parseColor("#828282"))
                                itemView.tvPlay.setText(R.string.het_luot_quay)
                            }
                        } else {
                            itemView.tvPlay.setTextColor(Color.parseColor("#828282"))
                            itemView.tvPlay.setText(R.string.het_luot_quay)
                        }
                    }
                    else -> {
                        itemView.imgUpcoming.setInvisible()
                        itemView.layoutRight.setInvisible()
                        itemView.tvDateRight.setInvisible()
                    }
                }

                itemView.tvDateRight.text = timeString
            }

            itemView.setOnClickListener {
                clickListener.onClick(obj)
            }
        }
    }
}
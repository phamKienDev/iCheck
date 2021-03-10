package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.the_winner_point

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_the_winner_time.view.*
import kotlinx.android.synthetic.main.item_the_winner_top.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.base.network.BaseModel
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKPointUser

internal class TheWinnerPointAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<BaseModel<ICKPointUser>>(callback) {

    fun addTopWinner(obj: BaseModel<ICKPointUser>) {
        listData.clear()
        listData.add(0, obj)
        notifyDataSetChanged()
    }

    fun setTheWinner(obj: MutableList<BaseModel<ICKPointUser>>) {
        checkLoadMore(obj.size)

        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type == ICKViewType.ITEM_TYPE) {
                listData.removeAt(i)
            }
        }

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addTheWinner(obj: MutableList<BaseModel<ICKPointUser>>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return when (listData[position].type) {
            ICKViewType.HEADER_TYPE -> ICKViewType.HEADER_TYPE
            else -> ICKViewType.ITEM_TYPE
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICKViewType.HEADER_TYPE -> TopWinnerHolder(parent)
            else -> TheWinnerHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is TopWinnerHolder -> {
                listData[position].listData?.let {
                    holder.bind(it)
                }
            }
            is TheWinnerHolder -> {
                listData[position].data?.let {
                    holder.bind(it)
                }
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    inner class TopWinnerHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICKPointUser>>(R.layout.item_the_winner_top, parent) {
        override fun bind(obj: MutableList<ICKPointUser>) {
            //Top 1
            WidgetHelper.loadImageUrl(itemView.imgAvatarTop1, obj[0].avatar?.medium, R.drawable.ic_circle_avatar_default)

            if (!obj[0].name.isNullOrEmpty()) {
                itemView.tvNameTop1.text = obj[0].name
            } else {
                itemView.tvNameTop1.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                itemView.tvNameTop1.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (obj[0].total_points != null) {
                itemView.tviCoinTop1.text = TextHelper.formatMoneyPhay(obj[0].total_points)
            } else {
                itemView.tviCoinTop1.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                itemView.tviCoinTop1.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            //Top 2
            if (obj.size > 1) {
                WidgetHelper.loadImageUrl(itemView.imgAvatarTop2, obj[1].avatar?.medium, R.drawable.ic_circle_avatar_default)

                if (!obj[1].name.isNullOrEmpty()) {
                    itemView.tvNameTop2.text = obj[1].name
                } else {
                    itemView.tvNameTop2.text = itemView.context.getString(R.string.dang_cap_nhat)
                }

                if (obj[1].total_points != null) {
                    itemView.tviCoinTop2.text = TextHelper.formatMoneyPhay(obj[1].total_points)
                } else {
                    itemView.tviCoinTop2.text = itemView.context.getString(R.string.dang_cap_nhat)
                }

                //Top 3
                if (obj.size > 2) {
                    WidgetHelper.loadImageUrl(itemView.imgAvatarTop3, obj[2].avatar?.medium, R.drawable.ic_circle_avatar_default)

                    if (!obj[2].name.isNullOrEmpty()) {
                        itemView.tvNameTop3.text = obj[2].name
                    } else {
                        itemView.tvNameTop3.text = itemView.context.getString(R.string.dang_cap_nhat)
                    }

                    if (obj[2].total_points != null) {
                        itemView.tviCoinTop3.text = TextHelper.formatMoneyPhay(obj[2].total_points)
                    } else {
                        itemView.tviCoinTop3.text = itemView.context.getString(R.string.dang_cap_nhat)
                    }
                } else {
                    itemView.tvNameTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                    itemView.tviCoinTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                    itemView.tviCoinTop3.textSize = 12f
                    itemView.tviCoinTop3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            } else {
                itemView.tvNameTop2.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                itemView.tviCoinTop2.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                itemView.tviCoinTop2.textSize = 12f
                itemView.tviCoinTop2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                itemView.tvNameTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                itemView.tviCoinTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray2))
                itemView.tviCoinTop3.textSize = 12f
                itemView.tviCoinTop3.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    inner class TheWinnerHolder(parent: ViewGroup) : BaseViewHolder<ICKPointUser>(R.layout.item_the_winner_time, parent) {
        override fun bind(obj: ICKPointUser) {

            itemView.tvTitle.visibleOrGone(adapterPosition == 1)

            itemView.tvName.run {
                text = if (!obj.name.isNullOrEmpty()) {
                    obj.name
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }

            itemView.tvPhone.run {
                text = if (!obj.phone.isNullOrEmpty()) {
                    try {
                        obj.phone.replaceRange(6, obj.phone.length, "****")
                    } catch (e: Exception) {
                        ""
                    }
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }
            itemView.tvDate.run {
                text = if (!obj.created_at.isNullOrEmpty()) {
                    TimeHelper.convertDateTimeSvToTimeDateVn(obj.created_at)
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }

            WidgetHelper.loadImageUrlRounded4(itemView.imgReward, obj.avatar?.medium)

            itemView.tvPoint.run {
                text = if (obj.points != null) {
                    "+ ${TextHelper.formatMoneyPhay(obj.points)}"
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }
        }
    }
}
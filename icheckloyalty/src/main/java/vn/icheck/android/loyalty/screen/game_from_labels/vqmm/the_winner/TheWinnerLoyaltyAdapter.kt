package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.the_winner

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.network.BaseModel
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKCampaign

internal class TheWinnerLoyaltyAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<BaseModel<ICKCampaign>>(callback) {

    fun addTopWinner(obj: BaseModel<ICKCampaign>) {
        listData.clear()
        listData.add(0, obj)
        notifyDataSetChanged()
    }

    fun setTheWinner(obj: MutableList<BaseModel<ICKCampaign>>) {
        checkLoadMore(obj.size)

        for (i in listData.size - 1 downTo 0) {
            if (listData[i].type == ICKViewType.ITEM_TYPE) {
                listData.removeAt(i)
            }
        }

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addTheWinner(obj: MutableList<BaseModel<ICKCampaign>>) {
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
            ICKViewType.HEADER_TYPE -> TopWinnerViewHolder(parent)
            else -> TheWinnerViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (holder) {
            is TopWinnerViewHolder -> {
                listData[position].listData?.let {
                    holder.bind(it)
                }
            }
            is TheWinnerViewHolder -> {
                listData[position].data?.let {
                    holder.bind(it)
                }
            }
        }
    }

    inner class TopWinnerViewHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICKCampaign>>(R.layout.item_top_winner_vqmm, parent) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: MutableList<ICKCampaign>) {
            (itemView as ConstraintLayout).run {
                val avatarTop1 = getChildAt(11) as CircleImageView
                val avatarTop2 = getChildAt(6) as CircleImageView
                val avatarTop3 = getChildAt(16) as CircleImageView

                val nameTop1 = getChildAt(13) as AppCompatTextView
                val nameTop2 = getChildAt(8) as AppCompatTextView
                val nameTop3 = getChildAt(18) as AppCompatTextView

                val coinTop1 = getChildAt(14) as AppCompatTextView
                val coinTop2 = getChildAt(9) as AppCompatTextView
                val coinTop3 = getChildAt(19) as AppCompatTextView

                //Top 1
                WidgetHelper.loadImageUrl(avatarTop1, obj[0].avatar?.medium, R.drawable.ic_circle_avatar_default)

                if (!obj.get(0).name.isNullOrEmpty()) {
                    nameTop1.text = obj.get(0).name
                } else {
                    nameTop1.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                    nameTop1.text = itemView.context.getString(R.string.dang_cap_nhat)
                }

                if (obj.get(0).winner_gifts?.get(0)?.gift?.icoin != null) {
                    coinTop1.text = "${TextHelper.formatMoneyPhay(obj.get(0).winner_gifts?.get(0)?.gift?.icoin)} Xu"
                } else {
                    coinTop1.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                    coinTop1.text = itemView.context.getString(R.string.dang_cap_nhat)
                }

                //Top 2
                if (obj.size > 1) {
                    WidgetHelper.loadImageUrl(avatarTop2, obj[1].avatar?.medium, R.drawable.ic_circle_avatar_default)

                    checkNullOrEmpty(nameTop2, obj.get(1).name)

                    if (obj.get(1).winner_gifts?.get(0)?.gift?.icoin != null) {
                        coinTop2.text = "${TextHelper.formatMoneyPhay(obj.get(1).winner_gifts?.get(0)?.gift?.icoin)} Xu"
                    } else {
                        coinTop2.text = itemView.context.getString(R.string.dang_cap_nhat)
                    }

                    //Top 3
                    if (obj.size > 2) {
                        WidgetHelper.loadImageUrl(avatarTop3, obj[2].avatar?.medium, R.drawable.ic_circle_avatar_default)

                        checkNullOrEmpty(nameTop3, obj.get(2).name)

                        if (obj.get(2).winner_gifts?.get(0)?.gift?.icoin != null) {
                            coinTop3.text = "${TextHelper.formatMoneyPhay(obj.get(2).winner_gifts?.get(0)?.gift?.icoin)} Xu"
                        } else {
                            coinTop3.text = itemView.context.getString(R.string.dang_cap_nhat)
                        }
                    } else {
                        nameTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                        coinTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                        coinTop3.textSize = 12f
                    }
                } else {
                    nameTop2.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                    coinTop2.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                    coinTop2.textSize = 12f

                    nameTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                    coinTop3.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorDisableText))
                    coinTop3.textSize = 12f
                }
            }
        }
    }

    inner class TheWinnerViewHolder(parent: ViewGroup) : BaseViewHolder<ICKCampaign>(R.layout.item_the_winner_vqmm, parent) {

        override fun bind(obj: ICKCampaign) {
            ((itemView as LinearLayout).getChildAt(0) as AppCompatTextView).visibility = if (adapterPosition == 1) {
                View.VISIBLE
            } else {
                View.GONE
            }

            (itemView.getChildAt(1) as ConstraintLayout).run {
                WidgetHelper.loadImageUrlRounded4((getChildAt(0) as AppCompatImageView), obj.winner_gifts?.get(0)?.gift?.image?.medium)

                checkNullOrEmpty((getChildAt(1) as AppCompatTextView), obj.name)

                checkNullOrEmptyConvertDateTimeSvToTimeDateVn((getChildAt(3) as AppCompatTextView), obj.created_at)

                (getChildAt(2) as AppCompatTextView).run {
                    text = if (!obj.phone.isNullOrEmpty()) {
                        try {
                            obj.phone?.replaceRange(6, obj.phone?.length!!, "****")
                        } catch (e: Exception) {
                            ""
                        }
                    } else {
                        default
                    }
                }

            }
        }
    }
}
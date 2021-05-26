package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.listofgiftsreceived

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gifts_received_game_loyalty.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKRewardGameVQMMLoyalty
import vn.icheck.android.loyalty.screen.gift_detail_from_app.GiftDetailFromAppActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

internal class ListOfGiftsReceivedAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICKRewardGameVQMMLoyalty>(callback) {
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

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKRewardGameVQMMLoyalty>(R.layout.item_gifts_received_game_loyalty, parent) {

        override fun bind(obj: ICKRewardGameVQMMLoyalty) {

            checkNullOrEmpty(itemView.tvName, obj.gift?.name)
            checkNullOrEmpty(itemView.tvNamePage, obj.gift?.owner?.name)
            checkNullOrEmptyConvertDateTimeSvToTimeDateVn(itemView.tvDate, obj.created_at)

            WidgetHelper.loadImageUrlRounded4(itemView.imgGift, obj.gift?.image?.medium)
            WidgetHelper.loadImageUrl(itemView.imgAvatar, obj.gift?.owner?.logo?.medium)


            itemView.setOnClickListener {
                when (obj.gift?.type) {
                    "ICOIN" -> {
                        LoyaltySdk.openActivity("point_transitions")
                    }
                    else -> {
                        itemView.context.startActivity(Intent(itemView.context, GiftDetailFromAppActivity::class.java).apply {
                            putExtra(ConstantsLoyalty.DATA_1, obj.winner_id)
                            putExtra(ConstantsLoyalty.DATA_2, obj.campaignId)
                        })
                    }
                }
            }
        }
    }
}
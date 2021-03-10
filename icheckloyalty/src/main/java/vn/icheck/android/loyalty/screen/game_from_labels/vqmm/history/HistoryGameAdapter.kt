package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.history

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_vqmm.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKItemReward

internal class HistoryGameAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<ICKItemReward>(callback) {

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

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKItemReward>(R.layout.item_history_vqmm, parent) {

        override fun bind(obj: ICKItemReward) {
            if (!SharedLoyaltyHelper(itemView.context).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM)) {
                checkNullOrEmpty(itemView.tvCode, obj.target)
            } else {
                checkNullOrEmpty(itemView.tvCode, obj.code)
            }

            checkNullOrEmptyConvertDateTimeSvToTimeDateVn(itemView.tvTime, obj.created_at)
        }
    }
}
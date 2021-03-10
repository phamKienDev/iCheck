package vn.icheck.android.screen.user.detail_my_reward.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.information_reward.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICItemReward

class InformationDetailRewardHolder(parent: ViewGroup) : BaseViewHolder<ICItemReward>(LayoutInflater.from(parent.context).inflate(R.layout.information_reward, parent, false)) {
    override fun bind(obj: ICItemReward) {
        itemView.containerShop.visibility = if (obj.type == 3) {
            View.VISIBLE
        } else {
            View.GONE
        }
        if (!obj.desc.isNullOrEmpty()) {
            itemView.tvInformation.text = obj.desc
        } else {
            itemView.tvInformation.text = itemView.context.getString(R.string.dang_cap_nhat)
        }
    }
}
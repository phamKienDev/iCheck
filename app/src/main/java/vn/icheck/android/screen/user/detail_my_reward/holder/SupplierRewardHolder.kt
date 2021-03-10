package vn.icheck.android.screen.user.detail_my_reward.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.supplier_reward.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.util.kotlin.WidgetUtils

class SupplierRewardHolder(parent: ViewGroup) : BaseViewHolder<ICItemReward>(LayoutInflater.from(parent.context).inflate(R.layout.supplier_reward, parent, false)) {
    override fun bind(obj: ICItemReward) {
        WidgetUtils.loadImageUrl(itemView.imgLogoSupplier, obj.shopImage)

        itemView.tvNameSupplier.text = if (!obj.shopName.isNullOrEmpty()) obj.shopName else itemView.context.getString(R.string.dang_cap_nhat)
    }
}
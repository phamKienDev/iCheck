package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemVendorV61Binding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.models.ICWidgetData

class ICVendorV61Holder(parent: ViewGroup, val binding: ItemVendorV61Binding = ItemVendorV61Binding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        binding.layoutTop.setBackgroundColor(ColorManager.getPrimaryColor(itemView.context))
        binding.tvPhone.setTextColor(ColorManager.getPrimaryColor(itemView.context))
        binding.tvPhone.setHintTextColor(ColorManager.getPrimaryColor(itemView.context))
        binding.tvAddress.setTextColor(ColorManager.getPrimaryColor(itemView.context))
        binding.tvAddress.setHintTextColor(ColorManager.getPrimaryColor(itemView.context))

    }
}
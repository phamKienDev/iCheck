package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemScanInfoBinding
import vn.icheck.android.network.models.ICWidgetData

class ICScanInfoHolder(parent: ViewGroup, val binding: ItemScanInfoBinding = ItemScanInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        binding.tvScanCount.text = (obj.scanCount ?: 0).toString()
        binding.tvPeopleCount.text = (obj.peopleCount ?: 0).toString()
    }
}
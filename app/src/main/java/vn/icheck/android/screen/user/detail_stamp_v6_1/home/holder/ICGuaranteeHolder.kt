package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemGuaranteeBinding
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICWidgetData

class ICGuaranteeHolder(parent: ViewGroup, val binding: ItemGuaranteeBinding = ItemGuaranteeBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        vn.icheck.android.ichecklibs.ColorManager.getSecondTextColor(itemView.context).apply {
            binding.tvTime.setHintTextColor(this)
            binding.tvEnd.setHintTextColor(this)
            binding.tvRemaining.setHintTextColor(this)
            binding.tvActivate.setHintTextColor(this)
        }
        binding.tvTime.text = "${obj.guaranteeDays} ${getTypeOfGuaranteeDay(obj.typeGuaranteeDay)}"
        binding.tvEnd.text = TimeHelper.convertDateTimeSvToDateVnStamp(obj.expireDate)
        binding.tvRemaining.text = itemView.context.getString(R.string.xxx_ngay, (obj.dayRemaining ?: 0).toString())
        binding.tvActivate.text = TimeHelper.convertDateTimeSvToDateVnStamp(obj.activeDate)
    }

    private fun getTypeOfGuaranteeDay(type: String?): String {
        return when (type) {
            "months" -> {
                binding.root.context.getString(R.string.thang_lowercase)
            }
            "years" -> {
                binding.root.context.getString(R.string.nam_lowercase)
            }
            else -> {
                ""
            }
        }
    }
}
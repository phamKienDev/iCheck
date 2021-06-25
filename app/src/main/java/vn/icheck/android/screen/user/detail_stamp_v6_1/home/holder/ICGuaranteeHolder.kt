package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemGuaranteeBinding
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.network.models.ICWidgetData

class ICGuaranteeHolder(parent: ViewGroup, val binding: ItemGuaranteeBinding = ItemGuaranteeBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        vn.icheck.android.ichecklibs.ColorManager.getSecondTextColor(itemView.context).apply {
            binding.tvTime.setHintTextColor(this)
            binding.tvEnd.setHintTextColor(this)
            binding.tvRemaining.setHintTextColor(this)
            binding.tvActivate.setHintTextColor(this)
        }

        if (obj.guaranteeDays != null) {
            (binding.tvTime.parent as ViewGroup).beVisible()
            binding.tvTime.text = "${obj.guaranteeDays} ${getTypeOfGuaranteeDay(obj.typeGuaranteeDay)}"
        } else {
            (binding.tvTime.parent as ViewGroup).beGone()
        }

        if (obj.expireDate != null) {
            (binding.tvEnd.parent as ViewGroup).beVisible()
            binding.tvEnd.text = TimeHelper.convertDateTimeSvToDateVnStamp(obj.expireDate)
        } else {
            (binding.tvEnd.parent as ViewGroup).beGone()
        }

        if (obj.dayRemaining != null) {
            (binding.tvRemaining.parent as ViewGroup).beVisible()
            binding.tvRemaining.text = itemView.context.getString(R.string.xxx_ngay, obj.dayRemaining.toString())
        } else {
            (binding.tvRemaining.parent as ViewGroup).beGone()
        }

        if (obj.activeDate != null) {
            (binding.tvActivate.parent as ViewGroup).beVisible()
            binding.tvActivate.text = TimeHelper.convertDateTimeSvToDateVnStamp(obj.activeDate)
        } else {
            (binding.tvActivate.parent as ViewGroup).beGone()
        }
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
                binding.root.context.getString(R.string.ngay_lowercase)
            }
        }
    }
}
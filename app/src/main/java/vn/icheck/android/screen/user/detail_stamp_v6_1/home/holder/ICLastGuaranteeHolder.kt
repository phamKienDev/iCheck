package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemLastGuaranteeBinding
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.screen.user.detail_stamp_v6_1.history_guarantee.HistoryGuaranteeActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class ICLastGuaranteeHolder(parent: ViewGroup, val binding: ItemLastGuaranteeBinding = ItemLastGuaranteeBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        binding.tvTime.text = TimeHelper.convertDateTimeSvToTimeDateVnStamp(obj.createTime)
        binding.tvAddress.text = obj.storeName
        binding.tvStatus.text = obj.status
        binding.tvState.text = obj.state

        binding.tvViewMore.setOnClickListener {
            if (!obj.serial.isNullOrEmpty()) {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityUtils.startActivity<HistoryGuaranteeActivity>(activity, Constant.DATA_1, obj.serial!!)
                }
            }
        }
    }
}
//package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import vn.icheck.android.R
//import vn.icheck.android.base.holder.BaseViewHolder
//import vn.icheck.android.databinding.ItemGuaranteeInfoBinding
//import vn.icheck.android.helper.TimeHelper
//import vn.icheck.android.network.models.ICWidgetData
//
//class ICVenderHolder(parent: ViewGroup, val binding: ItemGuaranteeInfoBinding = ItemGuaranteeInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {
//
//    override fun bind(obj: ICWidgetData) {
//        binding.tvTime.text = itemView.context.getString(R.string.xxx_ngay, (obj.guaranteeDay ?: 0).toString())
//        binding.tvEnd.text = TimeHelper.convertDateSvToDateVn(obj.expireDate)
//        binding.tvRemaining.text = itemView.context.getString(R.string.xxx_ngay, (obj.dayRemaining ?: 0).toString())
//        binding.tvActivate.text = TimeHelper.convertDateSvToDateVn(obj.activeDate)
//    }
//}
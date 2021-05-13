package vn.icheck.android.screen.user.detail_stamp_v6_1.home.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemProductInfoBinding
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICWidgetData
import vn.icheck.android.util.kotlin.WidgetUtils

class ICProductInfoHolder(parent: ViewGroup, val binding: ItemProductInfoBinding = ItemProductInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICWidgetData>(binding.root) {

    override fun bind(obj: ICWidgetData) {
        binding.tvPrice.apply {
            text = if (obj.price != null && obj.price != 0L) {
                TextHelper.formatMoneyComma(obj.price!!) + " đ"
            } else {
                null
            }
        }

        binding.tvName.text = obj.name
        binding.tvBarcode.text = obj.barcode

        WidgetUtils.loadImageUrl(binding.imgCountry, obj.ensign)
        binding.tvCountry.text = obj.country
    }
}
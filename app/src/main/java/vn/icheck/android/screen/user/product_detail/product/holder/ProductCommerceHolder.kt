package vn.icheck.android.screen.user.product_detail.product.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.databinding.ItemProductEcommerceBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICProductCommerce
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductCommerceHolder(parent: ViewGroup, val binding: ItemProductEcommerceBinding = ItemProductEcommerceBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICProductCommerce>(binding.root) {

    override fun bind(obj: ICProductCommerce) {
        WidgetUtils.loadImageUrlRounded(binding.imgAvatar, obj.avatar, R.drawable.ic_business_v2, SizeHelper.size4)

        binding.tvOldPrice.text = (TextHelper.formatMoney(obj.sellPrice) + " đ")
        binding.tvOldPrice.paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        binding.tvPrice.text = (TextHelper.formatMoney(obj.finalPrice) + " đ")
    }
}
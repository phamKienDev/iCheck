package vn.icheck.android.base.holder

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemProductEcommerceBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICProductLink
import vn.icheck.android.screen.user.webview.WebViewActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.WidgetUtils

class StampECommerceHolder(parent: ViewGroup, val binding: ItemProductEcommerceBinding = ItemProductEcommerceBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : BaseViewHolder<ICProductLink>(binding.root) {

    override fun bind(obj: ICProductLink) {
        WidgetUtils.loadImageUrlRounded(binding.imgAvatar, obj.images, R.drawable.ic_business_v2, SizeHelper.size4)

        binding.root.background=ViewHelper.btnWhiteStrokeLineColor0_5Corners4(binding.root.context)

        binding.tvName.text = obj.name

        if (obj.promotionPrice != null) {
            if (obj.listPrice != null) {
                binding.tvOldPrice.apply {
                    text = context.getString(R.string.s_d, TextHelper.formatMoneyPhay(obj.listPrice))
                    paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    beVisible()
                }
            } else {
                binding.tvOldPrice.beGone()
            }

            if (obj.promotionPrice != null) {
                binding.tvPrice.apply {
                    text = context.getString(R.string.s_d, TextHelper.formatMoneyPhay(obj.promotionPrice))
                    beVisible()
                }
            } else {
                binding.tvPrice.beGone()
            }
        } else {
            binding.tvOldPrice.beGone()
            binding.tvPrice.apply {
                text = context.getString(R.string.s_d, TextHelper.formatMoneyPhay(obj.listPrice))
                beVisible()
            }
        }

        itemView.setOnClickListener {
            if (!obj.link.isNullOrEmpty()) {
                WebViewActivity.openChrome(obj.link)
            }
        }
    }
}
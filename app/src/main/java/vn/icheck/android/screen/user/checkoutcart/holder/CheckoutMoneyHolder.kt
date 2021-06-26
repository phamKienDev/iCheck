package vn.icheck.android.screen.user.checkoutcart.holder

import android.view.View
import kotlinx.android.synthetic.main.item_checkout_money.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout

class CheckoutMoneyHolder(view: View) : BaseViewHolder<Checkout>(view) {

    override fun bind(obj: Checkout) {
        itemView.tvTotalPrice.text = itemView.context.getString(R.string.s_d, TextHelper.formatMoney(obj.sub_total))
        itemView.tvShippingPrice.text = itemView.context.getString(R.string.s_d, TextHelper.formatMoney(obj.shipping_amount))
        itemView.tvMoney.text = itemView.context.getString(R.string.s_d, TextHelper.formatMoney(obj.grand_total))
    }
}
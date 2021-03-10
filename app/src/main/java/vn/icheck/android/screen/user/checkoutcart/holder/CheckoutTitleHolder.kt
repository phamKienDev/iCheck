package vn.icheck.android.screen.user.checkoutcart.holder

import android.view.View
import kotlinx.android.synthetic.main.item_checkout_title.view.*
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView

class CheckoutTitleHolder(view: View, val listener: ICheckoutCartView) : BaseViewHolder<Checkout>(view) {

    override fun bind(obj: Checkout) {
        itemView.tvTitle.run {
            if (obj.title != null) {
                visibility = View.VISIBLE
                setText(obj.title!!)
            } else {
                visibility = View.INVISIBLE
            }
        }

        itemView.tvAction.run {
            if (obj.action != null) {
                visibility = View.VISIBLE
                setText(obj.action!!)
            } else {
                visibility = View.GONE
            }
        }

        itemView.tvAction.setOnClickListener {
            obj.shipping_address_id?.let { id ->
                listener.onSelectUserAddress(id)
            }
        }
    }
}
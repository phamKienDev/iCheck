package vn.icheck.android.screen.user.checkoutcart.holder

import android.view.View
import kotlinx.android.synthetic.main.item_checkout_add_address.view.*
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView

class CheckoutAddAddressHolder(view: View, val listener: ICheckoutCartView) : BaseViewHolder<ICAddress?>(view) {

    override fun bind(obj: ICAddress?) {
        val user = SessionManager.session.user
        itemView.tvName.text = (user?.last_name + " " + user?.first_name)
        itemView.tvPhone.text = user?.phone
        itemView.btnAdd.background = ViewHelper.btnWhiteStroke1Corners36(itemView.context)

        itemView.btnAdd.setOnClickListener {
            listener.onAddUserAddress()
        }
    }
}
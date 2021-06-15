package vn.icheck.android.screen.user.checkoutcart.holder

import android.view.View
import kotlinx.android.synthetic.main.item_checkout_address.view.*
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICAddress

class CheckoutAddressHolder(view: View) : BaseViewHolder<ICAddress>(view) {

    override fun bind(obj: ICAddress) {
        itemView.tvName.text = (obj.last_name + " " + obj.first_name)
        itemView.tvPhone.text = obj.phone
        itemView.tvAddress.text = (obj.address + getAddress(obj.ward?.name) + getAddress(obj.district?.name) + getAddress(obj.city?.name))
    }

    private fun getAddress(address: String?) : String {
        return if (!address.isNullOrEmpty()) {
            ", $address"
        } else {
            ""
        }
    }
}
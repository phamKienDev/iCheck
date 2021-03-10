package vn.icheck.android.screen.user.checkoutcart.entity

import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.network.models.ICCheckoutOrder
import vn.icheck.android.network.models.ICPayment

data class Checkout(
        val type: Int,
        var title: Int? = null,
        var action: Int? = null,
        var address: ICAddress? = null,
        var order: ICCheckoutOrder? = null,
        var payments: MutableList<ICPayment>? = null,
        var shipping_address_id: Long? = null,
        var payment_method_id: Int? = null,
        var sub_total: Long? = null,
        var shipping_amount: Long? = null,
        var grand_total: Long? = null
)
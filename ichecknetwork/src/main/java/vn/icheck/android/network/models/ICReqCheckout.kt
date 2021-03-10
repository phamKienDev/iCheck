package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqCheckout(
        @Expose val shipping_address_id: Long?,
        @Expose val payment_method_id: Int?,
        @Expose val orders: MutableList<ICReqOrder>?
)
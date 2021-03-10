package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICCheckout(
        @SerializedName("shipping_address_id") val shipping_address_id: Long?,
        @SerializedName("payment_method_id") val payment_method_id: Int,
        @SerializedName("payment_methods") val payment_methods: MutableList<ICPayment>,
        @SerializedName("orders") val orders: MutableList<ICCheckoutOrder>,
        @SerializedName("payment_fee") val payment_fee: Long,
        @SerializedName("sub_total") val sub_total: Long,
        @SerializedName("shipping_amount") val shipping_amount: Long,
        @SerializedName("grand_total") val grand_total: Long,
        @SerializedName("coupon_discount") val coupon_discount: Int
)
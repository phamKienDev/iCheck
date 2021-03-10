package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICRespCheckoutCart(
        @SerializedName("type") val type: String?,
        @SerializedName("url") val url: String?,
        @SerializedName("order_id") val order_id: String?
)
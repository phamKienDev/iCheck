package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICShipping(
        @SerializedName("shipping_amount") val shipping_amount: Long,
        @SerializedName("estimated_delivery_date_from") val estimated_delivery_date_from: String?,
        @SerializedName("estimated_delivery_date_to") val estimated_delivery_date_to: String?,
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("method") val method: ICShippingMethod
)
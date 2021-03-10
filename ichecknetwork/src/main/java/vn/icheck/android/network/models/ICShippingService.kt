package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICShippingService(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("code") val code: String,
        @SerializedName("method_id") val methodID: Int,
        @SerializedName("city_id") val cityID: Int?
)
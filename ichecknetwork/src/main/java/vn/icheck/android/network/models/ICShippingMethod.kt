package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICShippingMethod(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("provider") val provider: String?,
        @SerializedName("logo") val logo: String?,
        @SerializedName("is_active") val is_active: Boolean?
)
package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICVendorPage(
        @SerializedName("id") val id: Long = 0,
        @SerializedName("name") val name: String? = null,
        @SerializedName("verified") val verified: Boolean = false,
        @SerializedName("tax") val tax: String? = null,
        @SerializedName("website") val website: String? = null,
        @SerializedName("address") val address: String? = null,
        @SerializedName("phone") val phone: String? = null,
        @SerializedName("email") val email: String? = null,
        @SerializedName("country") val country: ICCountry = ICCountry()
) : Serializable
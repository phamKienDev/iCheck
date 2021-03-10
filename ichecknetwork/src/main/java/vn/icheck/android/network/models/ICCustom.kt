package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICCustom(
        @SerializedName("backgroundColor") val backgroundColor: String?,
        @SerializedName("fontColor") val fontColor: String?,
        @SerializedName("fontSize") val fontSize: String?
)
package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICRole(
        @SerializedName("id") val id: Long,
        @SerializedName("code") val code: String,
        @SerializedName("name") val name: String
)
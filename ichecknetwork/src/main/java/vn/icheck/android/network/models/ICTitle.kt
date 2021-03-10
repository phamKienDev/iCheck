package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICTitle(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String?
)
package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICCompany(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String,
        @SerializedName("image") var image: String?
):Serializable
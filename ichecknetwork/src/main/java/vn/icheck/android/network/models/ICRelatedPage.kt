package vn.icheck.android.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ICRelatedPage(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String? = null,
        @SerializedName("isVerify") val isVerify: Boolean,
        @SerializedName("isFollow") var isFollow: Boolean,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("corver") val corver: String?,
        @SerializedName("followCount") val followCount: Int
) : Serializable {
    val getName: String
        get() {
            return name ?: "Chưa cập nhật"
        }
}
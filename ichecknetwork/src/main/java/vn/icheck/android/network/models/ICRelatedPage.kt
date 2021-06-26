package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R
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
            return name ?: getString(R.string.chua_cap_nhat)
        }
}
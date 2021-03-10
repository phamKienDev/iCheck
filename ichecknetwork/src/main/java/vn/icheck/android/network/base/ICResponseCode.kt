package vn.icheck.android.network.base

import com.google.gson.annotations.SerializedName

open class ICResponseCode {
    @SerializedName("statusCode") var statusCode: String = ""
    @SerializedName("code") var code: Int = 0
    @SerializedName("message") var message: String? = "Không tải được dữ liệu. Vui lòng thử lại."
}
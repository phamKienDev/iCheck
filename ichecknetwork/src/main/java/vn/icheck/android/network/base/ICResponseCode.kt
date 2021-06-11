package vn.icheck.android.network.base

import com.google.gson.annotations.Expose

open class ICResponseCode {
    @Expose var statusCode: String = ""
    @Expose var status: String? = ""
    @Expose var code: Int = 0
    @Expose var message: String? = "Không tải được dữ liệu. Vui lòng thử lại."
}
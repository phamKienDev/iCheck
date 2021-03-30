package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.Expose

open class MCBaseResponse {
    @Expose
    var statusCode: Int? = -1
    @Expose
    var code:Int? = null
    @Expose
    var message: String? = null
}
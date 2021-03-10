package vn.icheck.android.network.base

import com.google.gson.annotations.Expose

open class ICBaseResponse {
    @Expose
    var statusCode: Int? = -1
    @Expose
    var code:Int? = null
    @Expose
    var message: String? = null
}
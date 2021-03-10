package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose

open class ICKBaseResponse {
    @Expose
    var statusCode: Int? = -1
    @Expose
    var code:Int? = null
    @Expose
    var message: String? = null
}
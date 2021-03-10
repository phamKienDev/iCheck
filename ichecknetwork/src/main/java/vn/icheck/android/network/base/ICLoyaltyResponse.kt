package vn.icheck.android.network.base

import com.google.gson.annotations.Expose

open class ICLoyaltyResponse {
    @Expose
    var code: Int? = -1
    @Expose
    var message: String? = null
}
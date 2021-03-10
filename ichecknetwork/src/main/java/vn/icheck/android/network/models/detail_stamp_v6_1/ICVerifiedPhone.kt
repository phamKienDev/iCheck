package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class ICVerifiedPhone {
    @Expose
    var data: Data? = null
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null

    class Data {
        @Expose
        var is_true: Boolean? = null
    }
}
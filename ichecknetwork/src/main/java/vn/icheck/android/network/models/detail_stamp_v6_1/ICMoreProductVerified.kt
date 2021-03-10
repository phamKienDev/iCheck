package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class ICMoreProductVerified {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ICListProductVerified? = null

    class ICListProductVerified {
        @Expose
        var products:MutableList<ICObjectListMoreProductVerified>? = null
    }
}
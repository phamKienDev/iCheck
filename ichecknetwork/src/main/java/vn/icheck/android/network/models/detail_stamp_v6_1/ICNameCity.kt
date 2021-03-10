package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class ICNameCity {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ICObjectCity? = null

    class ICObjectCity {
        @Expose
        var id:Int? = null
        @Expose
        var phone_code:String? = null
        @Expose
        var name:String? = null
        @Expose
        var code:String? = null
        @Expose
        var province_id:Int? = null
    }
}
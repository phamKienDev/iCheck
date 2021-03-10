package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class ICNameDistricts {

    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ICObjectDistricts? = null

    class ICObjectDistricts {
        @Expose
        var id: Int? = null
        @Expose
        var name: String? = null
        @Expose
        var city_id: Int? = null
    }

}
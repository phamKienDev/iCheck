package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class IC_Config_Error {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: ICStampConfig? = null
}
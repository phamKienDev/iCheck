package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.detail_stamp_v6.ICUpdateCustomerGuaranteeV6

class IC_RESP_UpdateCustomerGuarantee {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
//    @Expose
//    var data: ICUpdateCustomerGuaranteeV6? = null
    @Expose
    var message:String? = null
}
package vn.icheck.android.network.models.detail_stamp_v6

import com.google.gson.annotations.Expose

data class IC_RESP_UpdateCustomerGuaranteeV6(
        @Expose
        var error: Boolean? = null,
        @Expose
        var status: Int? = null,
        @Expose
        var data: ICUpdateCustomerGuaranteeV6? = null,
        @Expose
        var message: String? = null
)
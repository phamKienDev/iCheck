package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

data class ICGuaranteeCustomerDetail (
        @Expose
        var customer: ICGuaranteeCustomer? = null
) {
    class ICGuaranteeCustomer {
        @Expose
        var address: String? = null
        @Expose
        var city: Int? = null
        @Expose
        var country: String? = null
        @Expose
        var created_at: String? = null
        @Expose
        var device_id: String? = null
        @Expose
        var district: Int? = null
        @Expose
        var email: String? = null
        @Expose
        var icheck_id: String? = null
        @Expose
        var id: Long? = null
        @Expose
        var name: String? = null
        @Expose
        var phone: String? = null
        @Expose
        var point: Int? = null
        @Expose
        var status: String? = null
        @Expose
        var store_id: Long? = null
        @Expose
        var user_id: Long? = null
        @Expose
        val fields: MutableList<ICFieldGuarantee>? = null
    }
}
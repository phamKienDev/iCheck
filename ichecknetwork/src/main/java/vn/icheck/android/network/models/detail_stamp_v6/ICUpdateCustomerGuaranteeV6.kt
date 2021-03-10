package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose

data class ICUpdateCustomerGuaranteeV6(
    @Expose
    var name: String? = null,
    @Expose
    var city: String? = null,
    @Expose
    var address: String? = null,
    @Expose
    var phone: String? = null,
    @Expose
    var district: String? = null,
    @Expose
    var email: String? = null,
    @Expose
    var device_id: String? = null,
    @Expose
    var store_id: Int? = null,
    @Expose
    var id: Int? = null
)
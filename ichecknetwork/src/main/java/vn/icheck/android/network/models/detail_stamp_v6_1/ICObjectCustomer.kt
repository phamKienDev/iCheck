package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectCustomer(
        @Expose
        var city: Long? = null,
        @Expose
        var district: Long? = null,
        @Expose
        var address: String? = null,
        @Expose
        var email: String? = null,
        @Expose
        var phone: String? = null,
        @Expose
        var name: String? = null
): Serializable
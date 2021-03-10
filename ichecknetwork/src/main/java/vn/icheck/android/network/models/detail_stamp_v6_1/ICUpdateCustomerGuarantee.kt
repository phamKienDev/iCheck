package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICUpdateCustomerGuarantee (
        @Expose
        var name:String? = null,
        @Expose
        var phone:String? = null,
        @Expose
        var email:String? = null,
        @Expose
        var address:String? = null,
        @Expose
        var district:Int? = null,
        @Expose
        var city:Int? = null
) : Serializable